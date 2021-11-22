package my_help;

public class Errors {
    public static class WrongParameterGiven  extends Exception{
        public WrongParameterGiven(String details){
            super(details);
        }
    }

    public static void my_error(Exception e){
        System.out.println("ERROR");
        e.printStackTrace();
    }

    public static void outside_error(String m){
        System.out.println(m);
        System.exit(1);
    }
}
