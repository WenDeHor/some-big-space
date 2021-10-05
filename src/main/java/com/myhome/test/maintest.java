package com.myhome.test;

import java.io.FileNotFoundException;

public class maintest {
    public static void main(String[] args) throws FileNotFoundException {
        Animal one = new Animal();
        Animal two = new Cat();
        Cat three = new Cat();

        System.out.println(one.sayPrivateAnimal());
        System.out.println(one.say());
        System.out.println(one.say2());

        System.out.println(two.say2());
        System.out.println(two.say());

        System.out.println(three.say2());
        System.out.println(three.say());


//     List<List<String>>table=new ArrayList<>();
//     table.add(Arrays.asList("1","2","3"));
//        table.add(Arrays.asList("4","5"));
//        table.stream()
//                .limit(1)
//                .flatMap(Collection::stream)
//                .forEach(System.out::print);

//        Stream.of(2, 3, 0, 1)
//                .flatMapToInt(x -> IntStream.range(0, x))
//                .forEach(System.out::print);

//        Stream.concat(DoubleStream.of(1).boxed(), IntStream.of(2).boxed())
//                .map(java.lang.Object::getClass)
//                .forEach(System.out::print);

//        String[] count = {"One", "Two", "Three"};
////        Stream<String> numbers1 = Arrays.stream(count);
////        Stream<String> numbers2 = count.stream();
////
////        Stream<String> number3 = Stream.of(count);
////
////        Stream<String> numbers4 = Stream.concat(count)
////        Stream<String> numbers5 = ArrayStream.of(count);

//        Map<String, Integer> someMap = new HashMap<>();
//        Stream<Map.Entry<String, Integer>> entriesStream = someMap.entrySet().stream();
//        Stream<Integer> valuesStream = someMap.values().stream();
//        Stream<String> keysStream = someMap.keySet().stream();
//        Stream<Map.Entry<String, Integer>> entriesStream = someMap.stream();
//        Stream<Map.Entry<String, Integer>> entriesStream4 = Stream.of(someMap);

//        String number = "123456789";
//        Stream<Integer> numbers1 = number.chars();
//        IntStream numbers2 = number.chars();
//        IntStream numbers3 = number.codePoints();
//        Stream<Integer> numbers4 = number.chars().boxed();
//        Stream<Character> numbers5 = number.chars();

//       Object a1 = null, a2 = null, a3 = null;
//        Stream<Object> numbers1 = Stream.generate(a1, a2, a3);
//        Stream<Object> numbers2 = Stream.concat(a1, a2, a3);
//        Stream<Object> numbers3 = Stream.builder().add(a1).add(a2).add(a3).build();
//        Stream<Object> numbers4 = Arrays.stream(a1, a2, a3);
//        Stream<Object> numbers5 = Stream.of(a1, a2, a3);
//        Collectors.toList();

//        LocalDateTime dateTime=LocalDateTime.of(2017,11,26,15,38);
//        Period period=Period.ofYears(1).ofMonths(2).ofDays(3);
//        dateTime=dateTime.minus(period);
//        System.out.println(dateTime);

//        LocalDate date=LocalDate.of(2017,Month.NOVEMBER,8);
//        date=date.plus(48, ChronoUnit.HOURS);
//        System.out.println(date);

//        LocalDateTime localDateTime = LocalDateTime.of(2017, Month.NOVEMBER, 8, 15, 38);
//        String dt = localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
//        System.out.println(dt);

//        UserRepository usersRepository = null;
////        if (usersRepository.findTopByCounter() == null) {
//            int count = 100;
//            User user = User.builder()
//                    .password("sesedf")
//                    .login("userForm.getLogin()")
//                    .email("userForm.getEmail()")
////                    .counter(usersRepository.countByCounter())
//                    .address("address")
//                    .role(Role.ADMIN)
//                    .state(State.ACTIVE)
//                    .build();
//            System.out.println(user.toString());
//            usersRepository.save(user);
//        }
//        System.out.println(userRepository.findTopByCounter());;
//        Locale defaultLocale = Locale.getDefault();
//        System.out.println("Locale: " + defaultLocale.getDisplayName());
//        Currency currency = Currency.getInstance(defaultLocale);
//        System.out.println("Currency Code: " + currency.getCurrencyCode());
//        System.out.println("Symbol: " + currency.getSymbol());
//        System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());
//        System.out.println();

//        LocalDate date = LocalDate.now(); // получаем текущую дату
//        int year = date.getYear();
//        String month = String.valueOf(date.getMonthValue());
//
//        int dayOfMonth = date.getDayOfMonth();
//        DayOfWeek dayOfWeek = date.getDayOfWeek();
//        String format = date.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
//        System.out.println(format);
//        System.out.println(date);
//        System.out.println(dayOfWeek);
//
//        if (month.equals("4")) {
//            month = "match";
//        }
//
//        System.out.printf("%d.%s.%d \n", dayOfMonth, month, year);
//
//        Month month2 = date.getMonth();
//        System.out.printf("%d.%s.%d \n", dayOfMonth, month2, year);
//
//        String s = month2.toString().toLowerCase();
//        System.out.printf("%d.%s.%d \n", dayOfMonth, s, year);
    }
}
