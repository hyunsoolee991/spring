package expert001_01;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CarTest {

    @Test
    public void 자동차_장착_타이어브랜드_테스트() {
        Car car = new Car();

        Assertions.assertEquals("장착된 타이어: 코리아 타이어", car.getTireBrand());
    }

}