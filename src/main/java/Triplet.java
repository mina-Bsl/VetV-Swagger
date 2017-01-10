import io.swagger.models.auth.In;

/**
 * Created by boussalia.
 */
public class Triplet<Integer, String>{
        private final Integer First;
        private final String Second;
        private final String Third;

        public Triplet(Integer first, String second, String third) {
            First = first;
            Second = second;
            Third = third;
        }

    public Integer getFirst() {
        return First;
    }

    public String getSecond() {
        return Second;
    }

    public String getThird() {
        return Third;
    }
}
