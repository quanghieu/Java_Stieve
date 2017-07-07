
public class MyJniFunc {
	static {
	      System.loadLibrary("encrypt_hom"); // Load native library at runtime
	                                   // hello.dll (Windows) or libhello.so (Unixes)
	   }
	 
	   // Declare a native method sayHello() that receives nothing and returns void
	   public static native void Encrypt(String inputFile, String keyFile, String outputFile);
	 
	   // Test Driver
	   public static void main(String[] args) {
	      MyJniFunc.Encrypt("","","");  // invoke the native method
	   }
}
