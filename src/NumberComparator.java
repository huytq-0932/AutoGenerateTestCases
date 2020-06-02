/*
 * This code was generated by ojc.
 */
import hust.sqa.btl.instrument.*;


public class NumberComparator
{

    public NumberComparator()
    {
        super();
        trace.add( new java.lang.Integer( 1 ) );
    }

    public java.lang.String checkPrimeNumber( int number )
    {
        trace.add( new java.lang.Integer( 2 ) );
        if (number < 2) {
            trace.add( new java.lang.Integer( 3 ) );
            return "Number must be greater than 1";
        } else {
            trace.add( new java.lang.Integer( 4 ) );
        }
        boolean isPrime = true;
        for (int i = 2; i <= number / 2; i++) {
            trace.add( new java.lang.Integer( 5 ) );
            if (number % i == 0) {
                trace.add( new java.lang.Integer( 6 ) );
                isPrime = false;
                break;
            } else {
                trace.add( new java.lang.Integer( 7 ) );
            }
        }
        if (isPrime) {
            trace.add( new java.lang.Integer( 8 ) );
            return "Is Prime Number";
        } else {
            trace.add( new java.lang.Integer( 9 ) );
            return "Is Not Prime Number";
        }
    }

    
    static java.util.Set trace = new java.util.HashSet();

    
    public static void newTrace()
    {
        trace = new java.util.HashSet();
    }

    
    public static java.util.Set getTrace()
    {
        return trace;
    }

}