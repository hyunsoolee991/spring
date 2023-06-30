package expert006;

import javax.annotation.Resource;

public class Car {
    @Resource(name = "tire")
//	 @Autowired
//	 @Qualifier("tire2")
//	@Resource
    Tire tire;

    public String getTireBrand() {
        return "장착된 타이어: " + tire.getBrand();
    }
}