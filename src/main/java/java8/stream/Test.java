package java8.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yinkailun
 * @description:
 * @date 2019-08-09 10:24 AM
 */
public class Test {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

        list.parallelStream();
        Stream<String> stream = list.stream().filter(x -> {
            return x > 2;
        }).
//				sorted((a,b) -> {
//			return b-a;
//		}).
        map(c -> {
    return c + "@@@";
});
        stream.collect(Collectors.toList());
        System.out.println(stream);
    }

}
