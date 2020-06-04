import junit.framework.*;

public class TriangleTest extends TestCase {

  public static void main (String[] args) {
    junit.textui.TestRunner.run(TriangleTest.class);
  }

  @org.junit.jupiter.api.Test
  public void testCase1() {
    Triangle x3 = new Triangle();
    assertEquals("Is triangle scalene",String.valueOf(x3.checkTriangle(9, 10, 2)));
  }

  @org.junit.jupiter.api.Test
  public void testCase2() {
    Triangle x233 = new Triangle();
    assertEquals("Is triangle isosceles ",String.valueOf(x233.checkTriangle(4, 5, 4)));
  }

  @org.junit.jupiter.api.Test
  public void testCase3() {
    Triangle x435 = new Triangle();
    assertEquals("Is triangle equilateral ",String.valueOf(x435.checkTriangle(1, 1, 1)));
  }

  @org.junit.jupiter.api.Test
  public void testCase4() {
    Triangle x601 = new Triangle();
    assertEquals("Not a triangle",String.valueOf(x601.checkTriangle(-1, 0, 4)));
  }

}
