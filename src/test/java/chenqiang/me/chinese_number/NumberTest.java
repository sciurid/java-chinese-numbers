package chenqiang.me.chinese_number;

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;

public class NumberTest {
	
	@Test
	public void testChineseNumbers() {
		Random rnd = new Random(System.currentTimeMillis());
		for(int i = 0; i < 100; i++) {
			int value = rnd.nextInt();
			int scale = rnd.nextInt(10);
			BigDecimal dec = BigDecimal.valueOf(value, scale);
			System.out.println(String.format("%s -> %s", dec.toPlainString(), ChineseNumberUtils.toChinese(dec)));
		}
		
		for(int i = 0; i < 100; i++) {
			int value = Math.abs(rnd.nextInt());
			int scale = rnd.nextInt(3);
			BigDecimal dec = BigDecimal.valueOf(value, scale);
			System.out.println(String.format("%s -> %s", dec.toPlainString(), ChineseNumberUtils.toChineseAccounting(dec)));
		}
	}
	
}
