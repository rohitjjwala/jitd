
type type_t = string
type var_ref_t = string
type var_t = var_ref_t * type_t
type cog_t = string * var_t list

type pattern_t = (var_ref_t option * unlabeled_pattern_t)
and  unlabeled_pattern_t =
      | PCog of string * pattern_t list
      | PLeaf of type_t
      | PAny

type const_t =
  | CString of string
  | CBool of bool
  | CInt of int
  | CFloat of float

type bin_op_t =
  | Add
  | Multiply
  | Subtract
  | Divide

type cmp_op_t = Eq | Neq | Lt | Lte | Gt | Gte

type expr_t =
  | And       of expr_t list
  | Or        of expr_t list
  | Not       of expr_t
  | Cmp       of cmp_op_t * expr_t * expr_t
  | BinOp     of bin_op_t * expr_t * expr_t
  | Const     of const_t
  | Raw       of string
  | Var       of var_ref_t
  | RCog      of string * expr_t list

and  stmt_t =
      | Apply of rule_ref_t * expr_t
      | Let of var_t * expr_t
      | Rewrite of expr_t
      | IfThenElse of expr_t * stmt_t * stmt_t
      | Match of expr_t * (pattern_t * stmt_t) list
      | Block of stmt_t list
      | NoOp
and  rule_ref_t =
      | RuleRef   of string * rule_ref_t list
      | RuleParam of var_ref_t


type rule_t = string * var_t list * (pattern_t * stmt_t) list

type evt_t = string * var_ref_t list

type policy_t = string * var_t list * (evt_t * stmt_t) list

type program_t = {
  cogs  : cog_t list;
  rules : rule_t list;
  policies : policy_t list;
}

exception ParseError of string * Lexing.position

let empty_program = 
      { cogs = []; rules = []; policies = [] }
let add_cog  (p:program_t) (cog: cog_t)  =
      { cogs = cog :: p.cogs; rules = p.rules; policies = p.policies }
let add_rule (p:program_t) (rule:rule_t) =
      { cogs = p.cogs; rules = rule :: p.rules; policies = p.policies }
let add_policy (p:program_t) (policy:policy_t) =
      { cogs = p.cogs; rules = p.rules; policies = policy :: p.policies }
let merge_programs (a:program_t) (b:program_t) =
      { cogs = a.cogs @ b.cogs; 
        rules = a.rules @ b.rules;
        policies = a.policies @ b.policies }

let or_list  = function  Or(x) -> x | x -> [x]
let and_list = function And(x) -> x | x -> [x]
let rec binop_list (op:bin_op_t): (expr_t -> expr_t list) = function 
  | BinOp(other_op, x, y) 
      when op = other_op 
        -> ((binop_list op x) @ (binop_list op y))
  | x -> [x]

let mk_or a b  =  Or((or_list a)  @ (or_list b))
let mk_and a b = And((and_list a) @ (and_list b))

let get_rule (p:program_t) (name:string): rule_t =
  List.find (fun (rule_name, _, _) -> name == rule_name) p.rules

let stmt_children (f:stmt_t -> stmt_t list) = function
  | Apply _             -> []
  | Let _               -> []
  | Rewrite _           -> []
  | IfThenElse(_, t, e) -> [t, e]
  | Match(_,pat_stmt)   -> List.map snd pat_stmt
  | Block(stmts)        -> stmts
  | NoOp                -> []

let rebuild_stmt (old:stmt_t) (new_children:stmt_t list) = function
  | IfThenElse(c, _, _) -> IfThenElse(c, List.nth new_children 0, 
                                         List.nth new_children 1)
  | Match(tgt,pat_stmt) -> Match(tgt, List.map2 (fun old new -> (fst old, snd new))
                                                pat_stmt new_children)
  | Block(_)            -> Block(new_children)
  | x -> x


let string_of_var ((ref,t):var_t) = t ^ " " ^ ref
let string_of_cog ((c,vs):cog_t) = 
  ("cog "^c ^ "(" ^ (String.concat "," (List.map string_of_var vs)) ^ ");")
let rec string_of_pattern (x:pattern_t) = 
  match x with 
    | (None,PAny) -> "_"
    | (None,PLeaf(t)) -> t
    | (Some(s), PAny) -> s
    | (Some(s),PLeaf(t)) -> s^":"^t
    | (s,PCog(c, sub_patterns)) ->
        (match s with Some(s) -> s^":" | None -> "")^
        c^"("^(String.concat "," (List.map string_of_pattern sub_patterns))^")"

let string_of_cmp_op = function
  | Eq  -> "=="
  | Neq -> "!="
  | Lt  -> "<"
  | Lte -> "<="
  | Gt  -> ">"
  | Gte -> ">="
  
let string_of_bin_op = function
  | Add      -> "+"
  | Multiply -> "*"
  | Subtract -> "-"
  | Divide   -> "/"

let string_of_const = function
  | CInt(i)      -> string_of_int i
  | CFloat(f)    -> string_of_float f
  | CString(s)   -> "\""^s^"\""
  | CBool(true)  -> "true"
  | CBool(false) -> "false"

let rec string_of_expr = 
  let rcr = string_of_expr in function
  | And([])      -> string_of_const (CBool(true))
  | And(x::[])   -> "("^(rcr x)^")"
  | And(x::r)    -> (rcr x)^" and "^(rcr (And(r)))
  | Or([])       -> string_of_const (CBool(true))
  | Or(x::[])    -> "("^(rcr x)^")"
  | Or(x::r)     -> (rcr x)^" or "^(rcr (Or(r)))
  | Not(x)       -> "not ("^(rcr x)^")"
  | Cmp(op,a,b)  -> "("^(rcr a)^") "^(string_of_cmp_op op)^" ("^(rcr b)^")"
  | BinOp(op,a,b)-> "("^(rcr a)^") "^(string_of_bin_op op)^" ("^(rcr b)^")"
  | Const(c)     -> string_of_const c
  | Raw(e)       -> "@{"^e^"}"
  | Var(v)       -> v
  | RCog(c,params) -> c^"("^(String.concat ", " (List.map rcr params))^")" 

let rec string_of_stmt ?(prefix="")= 
  let rcr x = string_of_stmt ~prefix:(prefix^"  ") x in function
  | Apply(rule, tgt) -> 
      prefix^"apply " ^ (string_of_rule_ref rule) ^ " to " ^ (string_of_expr tgt)
  | Let(v, tgt) ->
      prefix^"let " ^(string_of_var v)^ " := " ^ (string_of_expr tgt)
  | Rewrite(expr) ->
      prefix^"rewrite " ^ (string_of_expr expr)
  | IfThenElse(cond, t, e) ->
      prefix^"if("^(string_of_expr cond)^")\n"^(rcr t)^"\n"^prefix^"else\n"^(rcr e)
  | Match(tgt, pats) ->
      prefix^"match "^(string_of_expr tgt)^" with {\n"^
      (string_of_match_list ~prefix:(prefix^"  ") pats)^"\n}"
  | Block(exps) -> 
    prefix^"{\n"^
    (String.concat ";\n" (List.map rcr exps))^"\n"^prefix^"}"
  | NoOp -> prefix^"done"
and string_of_match_list ?(prefix="") (pats:((pattern_t * stmt_t) list)) = 
  let rcr x = string_of_stmt ~prefix:(prefix^"  ") x in
    (String.concat "" (List.map (fun (pat, ex) ->
      prefix^"| "^(string_of_pattern pat)^" => \n"^(rcr ex)^"\n"
    ) pats))
and string_of_rule_ref = function
  | RuleRef(r, params) -> 
      r^"("^(String.concat "," (List.map string_of_rule_ref params))^")"
  | RuleParam(ex)      -> ex

let string_of_rule ((rule, args, pats):rule_t) =
  "rule "^rule^(match args with 
    | [] -> ""
    | _ -> "("^(String.concat ", " (List.map string_of_var args))^")"
  )^" is\n"^(string_of_match_list ~prefix:"  " pats)^";\n"

let string_of_event (((evt,args),stmt):(evt_t*stmt_t)) =
  "on "^evt^"("^(String.concat "," args)^") => \n" ^(string_of_stmt ~prefix:"  " stmt)

let string_of_policy ((name, args, evts):policy_t) = 
  "policy "^name^(match args with 
    | [] -> ""
    | _ -> "("^(String.concat ", " (List.map string_of_var args))^")"
  )^" is\n"^(String.concat "\n" (List.map string_of_event evts))^"\n;"

let string_of_program (prog:program_t) =
  (String.concat "\n" (List.map string_of_cog  prog.cogs))^"\n\n"^
  (String.concat "" (List.map string_of_rule prog.rules))^"\n"^
  (String.concat "\n" (List.map string_of_policy prog.policies))
