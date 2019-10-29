package java8.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yinkailun
 * @description:
 * @date 2019-08-09 10:24 AM
 */
public class TestForkJoin {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        list.parallelStream().filter(c -> {
            System.out.println(c + "," + Thread.currentThread());
            return c > 2;
        }).collect(Collectors.toSet());
    }
}

