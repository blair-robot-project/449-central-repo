package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.usfirst.frc.team449.robot.mixIn.UseCLASSIncludeWRAPPER_OBJECTMixIn;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

/** A module that adds mix-ins for various Java interfaces, classes, etc. */
public class JavaModule extends SimpleModule {

  /** Default constructor */
  public JavaModule() {
    super("JavaModule");
  }

  /**
   * Mixes in some mix-ins to the given context.
   *
   * @param context the context to set up
   */
  @Override
  public void setupModule(final SetupContext context) {
    super.setupModule(context);

    context.setMixInAnnotations(BooleanSupplier.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);
    context.setMixInAnnotations(DoubleSupplier.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);
    context.setMixInAnnotations(
        DoubleUnaryOperator.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);
    context.setMixInAnnotations(Runnable.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);

    //        context.setMixInAnnotations(Loggable.class, LoggableMixIn.class);
  }
}
