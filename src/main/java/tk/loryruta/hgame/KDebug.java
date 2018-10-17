package tk.loryruta.hgame;

// https://stackoverflow.com/questions/11306811/how-to-get-the-caller-class-in-java
public class KDebug {
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(KDebug.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getMethodName();
                }
            }
        }
        return null;
    }

    private KDebug() {
    }
}
