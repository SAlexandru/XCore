package ro;

public class Fibonacci {
	
	private int n_;
	
	public Fibonacci(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Argument to Fibonacci constructor has to greater than or equal to 0");
		}
		n_ = n;
	}
	
	public int n() { return n_; }
	
	private int recursiveHelper(int n) {
		if (n == 0) return 1;
		else if (n == 1) return 1;
		else if (n == 2) return 2;
		else return recursiveHelper(n - 1) + recursiveHelper(n - 2);
	}
	
	public int recursive() { return recursiveHelper(n_); }
	
	public int iterative() {
		int fib[] = {1, 1};
		
		for (int i = 2; i <= n_; ++i) {
			int f = fib[i - 1] + fib[i - 2];
			fib[0] = fib[1];
			fib[1] = f;
		}
		
		return fib[1];
	}
	

}
