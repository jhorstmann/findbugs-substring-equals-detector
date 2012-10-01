package net.jhorstmann.findbugs.substringequals;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack.CustomUserValue;
import edu.umd.cs.findbugs.OpcodeStack.Item;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

@CustomUserValue
public class SubstringEqualsDetector extends OpcodeStackDetector {

    static final class SubstringMarker {

        static final SubstringMarker INSTANCE = new SubstringMarker();

        private SubstringMarker() {
        }

        @Override
        public String toString() {
            return "SUBSTRING_MARKER";
        }
    }
    private final BugReporter bugReporter;

    public SubstringEqualsDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int opcode) {
        if (opcode == INVOKEVIRTUAL) {
            String className = getClassConstantOperand();
            String methodName = getNameConstantOperand();
            String methodSignature = getSigConstantOperand();
            if (className.equals("java/lang/String")) {
                if ((methodName.equals("equals") && methodSignature.equals("(Ljava/lang/Object;)Z")) || (methodName.equals("equalsIgnoreCase") && methodSignature.equals("(Ljava/lang/String;)Z")) || (methodName.equals("startsWith") && (methodSignature.equals("(Ljava/lang/String;)Z") || methodSignature.equals("(Ljava/lang/String;I)Z")))) {
                    int stringStackOffset = methodSignature.equals("(Ljava/lang/String;I)Z") ? 2 : 1;
                    Item stringItem = getStack().getStackItem(stringStackOffset);
                    if (stringItem.getUserValue() == SubstringMarker.INSTANCE) {
                        BugInstance bugInstance = new BugInstance(this, methodName.equals("startsWith") ? "SSE_SUBSTRING_STARTS_WITH" : "SSE_SUBSTRING_EQUALS", NORMAL_PRIORITY);

                        bugInstance.addClass(this);
                        bugInstance.addMethod(this);
                        bugInstance.addSourceLine(this);
                        bugReporter.reportBug(bugInstance);
                    }
                }
            }
        }
    }

    @Override
    public void afterOpcode(int opcode) {
        super.afterOpcode(opcode);
        if (opcode == INVOKEVIRTUAL) {
            String className = getClassConstantOperand();
            String methodName = getNameConstantOperand();
            String methodSignature = getSigConstantOperand();
            if (className.equals("java/lang/String")) {
                if (methodName.equals("substring") && (methodSignature.equals("(I)Ljava/lang/String;") || methodSignature.equals("(II)Ljava/lang/String;"))) {
                    Item topItem = getStack().getStackItem(0);
                    topItem.setUserValue(SubstringMarker.INSTANCE);
                }
            }
        }
    }
}
