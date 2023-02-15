package by.mitskevich;

import by.mitskevich.model.Animal;
import by.mitskevich.model.Car;
import by.mitskevich.model.Flower;
import by.mitskevich.model.House;
import by.mitskevich.model.Person;
import by.mitskevich.util.Util;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
//        task1();
//        task2();
//        task3();
//        task4();
//        task5();
//        task6();
//        task7();
//        task8();
//        task9();
//        task10();
//        task11();
//        task12();
//        task13();
//        task14();
        task15();
    }
    private static void task1() throws IOException {
        //https://www.baeldung.com/java-streams-multiple-filters-vs-condition#performance

        List<Animal> animals = Util.getAnimals();
        long animalsLimit = 7L;
        Predicate<Animal> isAgeBetween10And20 = animal -> animal.getAge() > 10 && animal.getAge() < 20;

        animals.stream()
                        .filter(isAgeBetween10And20)
                        .sorted(Comparator.comparing(Animal::getAge))//Age sort ASC
                        .skip(animalsLimit * 2)//Skip first 2 zoos
                        .limit(animalsLimit)//Our 7 Animals
                        .forEach(System.out::println);
    }


    private static void task2() throws IOException {
        List<Animal> animals = Util.getAnimals();
        Consumer<Animal> action = animal -> {
            if ("Female".equalsIgnoreCase(animal.getGender())) {
                animal.setBreed(animal.getBreed().toUpperCase());
            }
        };
                animals.stream()
                        .filter(animal -> "Japanese".equalsIgnoreCase(animal.getOrigin()))
                        .peek(action)
                        .map(Animal::getBreed)
                        .forEach(System.out::println);
    }

    private static void task3() throws IOException {
        List<Animal> animals = Util.getAnimals();
                animals.stream()
                        .filter(animal -> animal.getAge() > 30)
                        .map(Animal::getOrigin)
                        .filter(origin -> origin.startsWith("A"))
                        .distinct()
                        .forEach(System.out::println);
    }

    private static void task4() throws IOException {
        List<Animal> animals = Util.getAnimals();
        long count = animals.stream()
                            .filter(animal -> "Female".equalsIgnoreCase(animal.getGender()))
                            .count();

        System.out.println(count);
    }

    private static void task5() throws IOException {
        List<Animal> animals = Util.getAnimals();
        Predicate<Animal> isAgeBetween20And30 = animal -> animal.getAge() > 20 && animal.getAge() < 30;

        boolean isHungarian = animals.stream()
                .filter(isAgeBetween20And30)
                .anyMatch(animal -> "Hungarian".equalsIgnoreCase(animal.getOrigin()));

        System.out.println(isHungarian);
    }

    private static void task6() throws IOException {
        List<Animal> animals = Util.getAnimals();

        boolean isTraditionalGenderOnly = animals.stream()
                .allMatch(animal -> "Male".equalsIgnoreCase(animal.getGender()) ||
                        "Female".equalsIgnoreCase(animal.getGender()));

        System.out.println(isTraditionalGenderOnly);
    }

    private static void task7() throws IOException {
        List<Animal> animals = Util.getAnimals();
        boolean isAnyFromOceania = animals.stream()
                .noneMatch(animal -> "Oceania".equalsIgnoreCase(animal.getOrigin()));

        System.out.println(isAnyFromOceania);
    }

    private static void task8() throws IOException {
        List<Animal> animals = Util.getAnimals();
        OptionalInt maxAge = animals.stream()
                .sorted(Comparator.comparing(Animal::getBreed))
                .limit(100)
                .mapToInt(Animal::getAge)
                .max();

        System.out.println(maxAge.isPresent() ? maxAge.getAsInt() : "IS NOT PRESENT");
    }

    private static void task9() throws IOException {
        List<Animal> animals = Util.getAnimals();
        OptionalInt minSize = animals.stream()
                .map(Animal::getBreed)
                .map(String::toCharArray)
                .mapToInt(array -> array.length)
                .min();

        System.out.println(minSize.isPresent() ? minSize.getAsInt() : "IS NOT PRESENT");
    }

    private static void task10() throws IOException {
        List<Animal> animals = Util.getAnimals();
        int ageSum = animals.stream()
                .mapToInt(Animal::getAge)
                .sum();

        System.out.println(ageSum);
    }

    private static void task11() throws IOException {
        List<Animal> animals = Util.getAnimals();
        OptionalDouble indonesiaAverageAge = animals.stream()
                .filter(animal -> "Indonesian".equalsIgnoreCase(animal.getOrigin()))
                .mapToDouble(Animal::getAge)
                .average();

        System.out.println(indonesiaAverageAge.isPresent() ? indonesiaAverageAge.getAsDouble() : "IS NOT PRESENT");
    }

    private static void task12() throws IOException {
        List<Person> people = Util.getPersons();
        Predicate<Person> isAgeBetween18And27 = person -> {
            int age = LocalDate.now().minusYears(person.getDateOfBirth().getYear()).getYear();
            return age > 18 && age < 27;
        };

        people.stream()
                .filter(person -> "Male".equalsIgnoreCase(person.getGender()))
                .filter(isAgeBetween18And27)
                .sorted(Comparator.comparingInt(Person::getRecruitmentGroup))
                .limit(200)
                .forEach(System.out::println);
    }

    private static void task13() throws IOException {
        List<House> houses = Util.getHouses();
        Predicate<Person> ageUnder18 = person -> person.getDateOfBirth().getYear() > LocalDate.now().getYear() - 18;
        Predicate<Person> ageAfter65 = person -> person.getDateOfBirth().getYear() < LocalDate.now().getYear() - 60;

        Stream<Person> firstEvacWave = houses.stream()
                .filter(house -> "Hospital".equalsIgnoreCase(house.getBuildingType()))//HOSPITALS ONLY
                .flatMap(house -> house.getPersonList().stream());

        Stream<Person> secondEvacWave = houses.stream()
                .filter(house -> !"Hospital".equalsIgnoreCase(house.getBuildingType()))//NONE-HOSPITAL BUILDINGS -> ALREADY EVACUATED
                .flatMap(house -> house.getPersonList().stream())
                .filter(ageUnder18.or(ageAfter65));//age < 18 && age > 60

        Stream<Person> thirdEvacWave = houses.stream()
                .filter(house -> !"Hospital".equalsIgnoreCase(house.getBuildingType()))//NONE-HOSPITAL BUILDINGS -> ALREADY EVACUATED
                .flatMap(house -> house.getPersonList().stream())
                .filter((ageUnder18.or(ageAfter65)).negate());//18 < age < 60

        Stream.concat(Stream.concat(firstEvacWave, secondEvacWave), thirdEvacWave)
              .limit(500)
              .forEach(System.out::println);

    }



    private static void task14() throws IOException {
        List<Car> allCars = Util.getCars();
        //https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Non-interference
        CopyOnWriteArrayList<Car> cars = new CopyOnWriteArrayList<>(List.copyOf(allCars));

        List<Car> firstEchelon = cars.stream()
                .filter(car -> "Jaguar".equalsIgnoreCase(car.getCarMake()) ||
                        "White".equalsIgnoreCase(car.getColor()))
                .peek(cars::remove)
                .collect(Collectors.toList());

        List<Car> secondEchelon = cars.stream()
                .filter(car -> car.getMass() < 1500)
                .filter(car -> List.of("BMW", "Lexus", "Chrysler", "Toyota").contains(car.getCarMake()))
                .peek(cars::remove)
                .collect(Collectors.toList());

        List<Car> thirdEchelon = Stream.concat(
                        cars.stream()
                                .filter(car -> "Black".equalsIgnoreCase(car.getColor()))
                                .filter(car -> car.getMass() > 4000)
                                .peek(cars::remove),
                        cars.stream()
                                .filter(car ->
                                        "GMC".equalsIgnoreCase(car.getCarMake()) ||
                                                "Dodge".equalsIgnoreCase(car.getCarMake()))
                                .peek(cars::remove)
                )
                .collect(Collectors.toList());

        List<Car> fourthEchelon = cars.stream()
                .filter(car -> car.getReleaseYear() < 1982 ||
                        "Civic".equalsIgnoreCase(car.getCarModel()) ||
                        "Cherokee".equalsIgnoreCase(car.getCarModel()))
                .peek(cars::remove)
                .collect(Collectors.toList());

        List<Car> fifthEchelon = cars.stream()
                .filter(car ->
                                car.getPrice() > 40_000 ||
                                !("Yellow".equalsIgnoreCase(car.getColor())||
                                "Red".equalsIgnoreCase(car.getColor()) ||
                                "Green".equalsIgnoreCase(car.getColor()) ||
                                "Blue".equalsIgnoreCase(car.getColor())))
                .peek(cars::remove)
                .collect(Collectors.toList());

        List<Car> sixthEchelon = cars.stream()
                .filter(car -> car.getVin().contains("59"))
                .peek(cars::remove)
                .collect(Collectors.toList());

        cars.clear();

        double[] costsPerCountry = Stream.of(firstEchelon, secondEchelon, thirdEchelon, fourthEchelon, fifthEchelon, sixthEchelon)
                .mapToDouble(echelon -> echelon.stream()
                        .mapToDouble(car -> car.getMass() / 1000.0 * 7.14)
                        .sum())
                .toArray();

        AtomicInteger i = new AtomicInteger(1);
        double companyIncome = Arrays.stream(costsPerCountry)
                .peek(sum -> System.out.printf("Echelon number %d - %.2f $ \n", i.getAndIncrement(), sum))
                .sum();

        System.out.printf("Total company Income is - %.2f $", companyIncome);

    }

    /**
     * Задача №15 -
     * Для оранжереи нужно подобрать растения соответствующие требованиям.
     * Во-первых, нужно произвести сложную сортировку каталога растений.
     * Отсортировать их по странам происхождения в обратном порядке
     * После по стоимости и еще по водопотреблению в обратном порядке.
     * Из этого списка взять растения название которых
     * от буквы "S" до буквы "C".
     * Если растения тенелюбивые и им подходит горшок из стекла, алюминия или стали - то выбираем их.
     * Далее на каждое растение надо рассчитать стоимость растения + стоимость потребления воды за 5 лет c учётом того
     * что кубометр воды стоит 1.39 $. Суммировать общую стоимость обслуживания всех растений. Во сколько это обойдётся бюджету?
     */
    private static void task15() throws IOException {
        List<Flower> flowers = Util.getFlowers();
        final double costPerLitre = 365 * 5 * 1.39 / 1000;
        Set<String> vasesMaterial = Set.of("Glass", "Aluminium", "Steel");
        //Cubic meter of water = 1000 L


        double totalCosts = flowers.stream()
                .sorted(Comparator.comparing(Flower::getOrigin).reversed()
                                  .thenComparing(Flower::getPrice)
                                  .thenComparing(Flower::getWaterConsumptionPerDay).reversed())
                .filter(flower -> Pattern.compile("([C-S])(.+)").asPredicate().test(flower.getCommonName()))
                .filter(Flower::isShadePreferred)
                .filter(flower -> flower.getFlowerVaseMaterial().stream()
                                  .anyMatch(vasesMaterial::contains))
                .mapToDouble(flower -> flower.getPrice() + flower.getWaterConsumptionPerDay() * costPerLitre)
                .sum();

        System.out.printf("Total costs are - %.2f $", totalCosts);

    }
}