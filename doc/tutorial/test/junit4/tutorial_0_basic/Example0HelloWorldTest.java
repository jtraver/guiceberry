package junit4.tutorial_0_basic;

import static org.junit.Assert.assertTrue;

import com.google.guiceberry.GuiceBerryModule;
import com.google.guiceberry.junit4.GuiceBerryRule;

import org.junit.Rule;
import org.junit.Test;

public class Example0HelloWorldTest {

  @Rule
  public GuiceBerryRule guiceBerry = new GuiceBerryRule(Env.class);

  @Test
  public void testNothing() throws Exception {
    assertTrue(true);
  }

  public static final class Env extends GuiceBerryModule {}
}