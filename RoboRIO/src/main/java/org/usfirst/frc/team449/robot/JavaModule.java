package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.usfirst.frc.team449.robot.mixIn.DoubleUnaryOperatorMixIn;

import java.util.function.DoubleUnaryOperator;

/**
 * A module that adds mix-ins for various Java interfaces, classes, etc.
 */
public class JavaModule extends SimpleModule {

    /**
     * Default constructor
     */
    public JavaModule() {
        super("JavaModule");
    }

    /**
     * Mixes in some mix-ins to the given context.
     *
     * @param context the context to set up
     */
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.setMixInAnnotations(DoubleUnaryOperator.class, DoubleUnaryOperatorMixIn.class);
    }
}
