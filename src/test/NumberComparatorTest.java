import junit.framework.*;

public class NumberComparatorTest extends TestCase {

  public static void main (String[] args) {
    junit.textui.TestRunner.run(NumberComparatorTest.class);
  }

  @org.junit.jupiter.api.Test
  public void testCase1() {
    NumberComparator x1 = new NumberComparator();
    assertEquals("Number must be greater than 1",String.valueOf(x1.checkPrimeNumber(-63980)));
  }

  @org.junit.jupiter.api.Test
  public void testCase2() {
    NumberComparator x61 = new NumberComparator();
    assertEquals("Is Not Prime Number",String.valueOf(x61.checkPrimeNumber(5646)));
  }

  @org.junit.jupiter.api.Test
  public void testCase3() {
    NumberComparator x121 = new NumberComparator();
    assertEquals("Is Not Prime Number",String.valueOf(x121.checkPrimeNumber(28908)));
  }

  @org.junit.jupiter.api.Test
  public void testCase4() {
    NumberComparator x183 = new NumberComparator();
    assertEquals("Is Not Prime Number",String.valueOf(x183.checkPrimeNumber(50965)));
  }

  @org.junit.jupiter.api.Test
  public void testCase5() {
    NumberComparator x295 = new NumberComparator();
    assertEquals("Is Prime Number",String.valueOf(x295.checkPrimeNumber(33563)));
  }

  @org.junit.jupiter.api.Test
  public void testCase6() {
    NumberComparator x303 = new NumberComparator();
    assertEquals("Is Not Prime Number",String.valueOf(x303.checkPrimeNumber(3755)));
  }

}
