package jitd.test;

import java.util.*;

import jitd.*;

public class CogTest {
    
  Random rand = new Random();

  public Cog leaf(long k)
  {
    return new LeafCog(k, rand.nextLong());
  }
  
  public ArrayCog array(long[] ks)
  {
    ArrayCog ret = new ArrayCog(ks.length);
    ret.keys = ks;
    for(int i = 0; i < ks.length; i++){
      ret.values[i] = rand.nextLong();
    }
    return ret;
  }
  
  public Cog sorted(long[] ks)
  {
    long[] tmp = Arrays.copyOf(ks, ks.length);
    Arrays.sort(tmp);
    ArrayCog ret = array(tmp);
    ret.sorted = true;
    return ret;
  }
  
  public Cog empty()
  {
    return new SubArrayCog(null, 0, 0);
  }
  
  public Cog concat(Cog a, Cog b)
  {
    return new ConcatCog(a, b);
  }
  
  public Cog bnode(long sep, Cog a, Cog b)
  {
    return new BTreeCog(sep, a, b);
  }

  final long[] test1 = new long[] {
    108, 141, 152, 113, 246, 97, 94, 170, 126, 192, 169, 87, 236, 182, 241, 95 
  };
  final long[] test2 = new long[] {
    161, 138, 63, 92, 242, 221, 137
  };
  final long[] test3 = new long[] {
    201, 62, 90, 177, 226, 176, 178, 131, 131, 200, 55, 166, 196, 108, 137, 
    66, 140, 102, 63, 123, 113, 123, 154, 63, 56, 249, 119, 233, 97, 134, 
    221, 245, 174, 126, 65, 196, 79, 233, 249, 69, 137, 150, 163, 103, 110, 
    142, 154, 196, 87, 182, 166, 233, 137, 184, 182, 191, 95, 216, 140, 178, 
    178, 168, 243, 249, 76, 87, 128, 199, 238, 116, 107, 111, 236, 237, 127, 
    104, 98, 189, 210, 185, 244, 61, 150, 184, 128, 118, 73, 54, 119, 245, 
    195, 222, 104, 225, 214, 242, 143, 200, 233, 126, 91, 199, 50, 75, 95, 
    135, 102, 63, 193, 52, 240, 214, 118, 115, 224, 155, 64, 78, 185, 79, 
    214, 63, 130, 149, 233, 71, 236, 144, 160, 107, 97, 196, 54, 51, 119, 
    58, 67, 230, 151, 158, 201, 100, 55, 126, 67, 96, 210, 141, 81, 166, 
    148, 244, 129, 76, 187, 128, 228, 108, 144, 138, 87, 158, 230, 182, 70, 
    124, 153, 128, 86, 77, 76, 249, 140, 189, 75, 65, 201, 100, 70, 200, 
    204, 159, 224, 116, 248, 138, 229, 182, 82, 235, 85, 161, 240, 114, 235, 
    206, 59, 63, 238, 170, 174, 153, 105, 234, 148, 191, 243, 165, 53, 220, 
    235, 182, 161, 163, 218, 167, 201, 207, 163, 101, 159, 173, 165, 53, 
    161, 62, 84, 218, 55, 169, 56, 145, 100, 56, 79, 85, 234, 241, 152, 107, 
    186, 83, 114, 160, 243, 68, 117, 166, 212, 112, 159, 116, 175, 206, 66, 
    238, 106, 104, 101, 146, 141, 186, 140, 183, 79, 180, 188, 89, 111, 55, 
    77, 70, 202, 223, 171, 112, 211, 135, 99, 125, 51, 208, 169, 169, 209, 
    199, 199, 217, 153, 234, 172, 136, 122, 218, 191, 86, 207, 144, 179, 
    164, 158, 129, 227, 230, 95, 181, 186, 116, 178, 137, 55, 192, 224, 188, 
    123, 238, 86, 206, 109, 230, 239, 241, 201, 218, 160, 57, 151, 161, 56, 
    219, 153, 107, 67, 245, 107, 185, 90, 108, 224, 176, 95, 204, 214, 221, 
    124, 126, 179, 84, 134, 237, 192, 55, 78, 84, 148, 67, 224, 232, 155, 
    71, 133, 237, 80, 132, 166, 59, 85, 116, 202, 64, 220, 210, 67, 150, 
    166, 140, 93, 232, 247, 238, 171, 147, 220, 228, 55, 177, 156, 191, 200, 
    150, 205, 112, 183, 151, 238, 249, 142, 91, 248, 170, 63, 149, 192, 108, 
    203, 208, 229, 157, 204, 123, 136, 50, 158, 177, 230, 187, 121, 226, 87, 
    118, 186, 225, 55, 220, 68, 101, 74, 122, 177, 148, 66, 207, 82, 213, 
    202, 171, 221, 139, 226, 97, 189, 129, 232, 138, 54, 222, 161, 246, 154, 
    204, 184, 115, 179, 132, 64, 68, 102, 79, 94, 163, 118, 157, 186, 212, 
    67, 73, 179, 174, 183, 225, 85, 118, 200, 89, 217, 245, 209, 226, 183, 
    76, 96, 102, 99, 55, 238, 224, 239, 79, 118, 158, 228, 140, 149, 172, 
    58, 209, 198, 128, 202, 133, 134, 100, 226, 246, 119, 136, 72, 96, 149, 
    79, 98, 229, 164, 162, 172, 204, 128, 229, 246, 231, 243, 109, 120, 244, 
    50, 241, 120, 146, 184, 136, 137, 188, 74, 117, 113, 162, 154, 86, 228, 
    95, 157, 239, 201, 234, 140, 206, 87, 149, 143, 89, 99, 206, 117, 226, 
    200, 99, 247, 91, 93, 88, 104, 84, 182, 86, 80, 181, 64, 123, 113, 59, 
    65, 58, 122, 129, 65, 98, 121, 203, 202, 184, 83, 154, 87, 249, 104, 
    207, 206, 178, 180, 166, 96, 234, 231, 82, 146, 207, 225, 50, 67, 209, 
    187, 208, 168, 194, 244, 114, 106, 109, 182, 108, 55, 223, 120, 65, 75, 
    123, 107, 120, 219, 243, 227, 65, 130, 170, 238, 120, 76, 236, 229, 198, 
    57, 153, 188, 243, 167, 142, 209, 58, 144, 59, 190, 229, 101, 119, 241, 
    67, 75, 222, 99, 79, 182, 168, 121, 63, 139, 246, 70, 86, 198, 104, 152, 
    154, 86, 91, 180, 150, 60, 88, 148, 50, 136, 228, 248, 238, 236, 239, 
    53, 121, 153, 72, 114, 210, 91, 163, 82, 98, 81, 245, 58, 190, 238, 189, 
    77, 215, 227, 101, 82, 155, 114, 246, 154, 200, 112, 241, 175, 230, 199, 
    132, 200, 67, 137, 166, 214, 104, 74, 176, 221, 60, 154, 76, 64, 198, 
    127, 141, 195, 141, 83, 181, 63, 50, 122, 163, 233, 126, 226, 187, 125, 
    174, 158, 195, 149, 87, 85, 187, 72, 157, 154, 243, 196, 150, 205, 55, 
    81, 165, 226, 95, 156, 136, 124, 205, 109, 201, 122, 156, 149, 89, 234, 
    71, 119, 104, 155, 53, 82, 152, 235, 227, 182, 205, 121, 151, 95, 213, 
    73, 62, 205, 75, 103, 185, 154, 129, 132, 87, 155, 155, 92, 61, 229, 
    136, 210, 83, 144, 234, 174, 143, 195, 160, 93, 70, 75, 71, 94, 82, 
    144, 95, 214, 227, 140, 241, 74, 56, 86, 179, 185, 126, 102, 130, 52, 
    222, 214, 248, 160, 141, 96, 207, 177, 197, 59, 200, 64, 149, 184, 140, 
    226, 131, 93, 230, 245, 98, 189, 55, 232, 237, 198, 160, 96, 178, 214, 
    177, 244, 228, 82, 71, 205, 218, 53, 217, 74, 196, 221, 174, 169, 70, 
    161, 210, 173, 133, 60, 207, 120, 85, 219, 145, 248, 69, 85, 111, 224, 
    234, 193, 151, 234, 190, 131, 243, 168, 218, 64, 173, 226, 224, 142, 
    228, 224, 176, 78, 95, 159, 238, 224, 209, 191, 155, 100, 237, 100, 61, 
    136, 104, 69, 112, 127, 232, 73, 118, 90, 114, 187, 142, 108, 186, 211, 
    157, 105, 210, 198, 148, 124, 232, 191, 240, 233, 76, 236, 148, 218, 
    197, 50, 184, 216, 75, 132, 122, 85, 152, 89, 210, 94, 185, 96, 149, 
    143, 164, 110, 246, 141, 66, 91, 133, 214, 224, 95, 143, 168, 228, 67, 
    64, 158, 129, 213, 231, 166, 247, 180, 51, 170, 180, 90, 107, 69, 147, 
    232, 69, 55, 181, 105, 176, 237, 170, 127, 174
  };
  
  public long[] randArray(int size)
  {
    long[] ret = new long[size];
    for(int i = 0; i < size; i++){
      ret[i] = Math.abs(rand.nextLong()) % 200 + 50;
    }
    return ret;
  }
  

}