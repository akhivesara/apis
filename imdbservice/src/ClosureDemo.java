
import java.util.function.Function;

public class ClosureDemo {

    public static void main(String[] args) {
        closureType1Demo();
        closureType2Demo();
        closureType3Demo();
        closureType4Demo();
        closureType5Demo();
    }

	/*
	CLOSURES:
	A closure is the combination of a function bundled together (enclosed) with
	references to its surrounding state (the lexical environment). In other words,
	a closure gives you access to an outer function’s scope from an inner function.

	Closures are commonly used to give objects data privacy. When you use closures for data privacy,
	the enclosed variables are only in scope within the containing (outer) function.
	You can’t get to the data from an outside scope except through the object’s privileged methods.
	In JavaScript, any exposed method defined within the closure scope is privileged.

	Closures are also used when we need to partially apply functions and also for currying. The returned partial function is the privileged function
	that remembers the applied parameters.

	Closures are basically stateful functions
	 */

    @FunctionalInterface // optional
    public interface NumToTextConverter {
        String convert(int x);
    }

    static void closureType1Demo() {
        NumToTextConverter textOfWeekday = new NumToTextConverter() {
            String [] weeks = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            @Override
            public String convert(int num) {
                return (num > 0 && num <= weeks.length) ? weeks[num-1] : null;
            }
        };
        System.out.println(textOfWeekday.convert(1)); // Mon
    }

    // Type 2: Closure with custom Functional Interface & Lambda expression
    static void closureType2Demo() {
        NumToTextConverter textOfWeekday = num -> {
            String [] weeks = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            return (num > 0 && num <= weeks.length) ? weeks[num-1] : null;
        };
        System.out.println(textOfWeekday.convert(2)); // Tue
    }

    // Type 3: Closure with predefined Functional Interface, with Lambda expression

    static void closureType3Demo() {
        Function<Integer, String> getTextOfWeekday = num -> {
            String [] weeks = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            return (num > 0 && num <= weeks.length) ? weeks[num-1] : null;
        };
        System.out.println(getTextOfWeekday.apply(3)); // Wed
    }

    // Type 4: Closure with predefined Functional Interface, with Lambda expression,
    // with inner function having access to parent scope (String [] weeks)
    static Function<Integer, String> getTextOfWeekday() {
        String [] weeks = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        // privileged inner function that encloses/remembers weeks
        return num -> (num > 0 && num <= weeks.length) ? weeks[num-1] : null;

    }
    static void closureType4Demo() {
        System.out.println(getTextOfWeekday().apply(4)); // Thu
    }

    // Type 5: Closure with predefined Functional Interface, with Lambda expression,
    // with inner function having access to parent scope
    // parent scope, in this scope, is nothing but state passed by client
    static Function<Integer, String> getTextOfWeekday(String [] weeks) {
        return num -> (num > 0 && num <= weeks.length) ? weeks[num-1] : null;
    }
    static void closureType5Demo() {
        Function<Integer, String> getArabTextOfWeekday = getTextOfWeekday(
                new String[]{ "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu"}
        );
        Function<Integer, String> getIndianTextOfWeekday = getTextOfWeekday(
                new String[]{ "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}
        );
        System.out.println(getArabTextOfWeekday.apply(5)); // Tue
        System.out.println(getIndianTextOfWeekday.apply(5)); // Fri
    }

}
