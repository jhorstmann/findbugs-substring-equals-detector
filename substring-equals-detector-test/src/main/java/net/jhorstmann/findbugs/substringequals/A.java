package net.jhorstmann.findbugs.substringequals;

public class A {
    public static void main(String[] args) {
        String str = "abcdef";
        System.out.println(str.substring(3, str.length()).equals("def"));
        System.out.println(str.substring(3, str.length()).equalsIgnoreCase("DEF"));
        System.out.println(str.substring(3, str.length()).startsWith("def"));
        System.out.println(str.substring(3, str.length()).startsWith("def", 0));
    }

}
