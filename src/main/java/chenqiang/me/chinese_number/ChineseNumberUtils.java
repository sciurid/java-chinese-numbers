package chenqiang.me.chinese_number;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * Format numbers to Chinese representation. For normal use and for accounting use.
 * 将数字转化为对应的中文表示。
 * 支持一般的中文大写数字（整数和小数），最大不超过一万亿。支持财务表示的中文大写金额，不超过一万亿，精度最大到小数点后两位（分）。
 * @author CHEN Qiang
 *
 */
public class ChineseNumberUtils {
	/**
	 * 支持表示的整数部分的最大值，为一万亿。（一般情况下，超过这个量级的数字一般都使用阿拉伯数字或者科学计数法等其他方式了。）
	 */
	public static final BigInteger MAX_VALUE = BigInteger.valueOf(10).pow(12);
	private static final Pattern DIGITS = Pattern.compile("[\\d]+");
	

	private static final String [] CHINESE_NUMBERS = new String [] {
		"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"
	};
	private static final String [] CHINESE_UNITS = new String [] {"", "十", "百", "千"};
	
	private static final String [] CHINESE_CAPITAL_NUMBERS = {
		"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"
	};
	private static final String [] CHINESE_CAPITAL_UNITS = {"", "拾", "佰", "仟"};
	
	/**
	 * 数字以万、亿为分割单位后的每个部分（小于一万）的中文表示。
	 * @param value 要表示的整数数字，不超过10000。
	 * @param sb 目标字符缓冲区，中文表示将追加至尾部。
	 * @param numbers 中文数码数组。
	 * @param units 中文单位数组。
	 * @return
	 */
	private static StringBuilder convertNumberPart(int value, StringBuilder sb, final String [] numbers, final String [] units) {
		assert value >= 0 && value < 10000;
		
		int thousand = value / 1000;
		int hundred = value / 100 % 10;
		int ten = value / 10 % 10;
		int remainer = value % 10;
		
		//千位
		if(thousand > 0) {
			sb.append(numbers[thousand]).append(units[3]);
		}
		//百位
		if(hundred > 0) {
			sb.append(numbers[hundred]).append(units[2]);
		}
		//十位
		if(ten > 0) {
			//且千位有值但是百位为零，则增加一个“零”
			if(thousand > 0 && hundred == 0) {
				sb.append("零");
			}
			if(ten > 1) {
				sb.append(numbers[ten]);
			}
			sb.append(units[1]);
		}
		if(remainer > 0) {
			//如果千位或者百位有值但是十位为零时，则增加一个“零”
			if(ten == 0 && (thousand > 0 || hundred > 0)) {
				sb.append("零");
			}
			sb.append(numbers[remainer]);
		}
		return sb;
	}
	
	/**
	 * 中文的纯数字序列表示。
	 * @param digits 要表示的数字序列。
	 * @param sb 目标字符缓冲区，中文表示将追加至尾部。
	 * @param numbers 中文数码表示。
	 * @return
	 */
	private static StringBuilder convertSerial(String digits, StringBuilder sb, final String [] numbers) {
		assert DIGITS.matcher(digits).matches();
		
		for(int i = 0; i < digits.length(); i++) {
			int digit = Integer.valueOf(digits.substring(i, i+1));
			sb.append(numbers[digit]);
		}
		return sb;
	}
	
	
	/**
	 * 中文的整数数字表示，用亿、万、个三个部分拼接而成。
	 * @param value 要表示的数字。
	 * @param numbers 中文数码数组。
	 * @param units 中文单位数组。
	 * @return 中文表示结果。
	 */
	public static String convertInteger(BigInteger value, final String [] numbers, final String [] units) {
		assert MAX_VALUE.compareTo(value) > 0;
		long lval = value.longValue();
		if(lval == 0) {
			return "零";
		}
		
		StringBuilder buffer = new StringBuilder();
		
		if(lval < 0){
			buffer.append("负");
			lval *= -1;
		}
		
		int yi = (int)(lval / 100000000);
		int wan = (int)((lval / 10000) % 10000);
		int remainer = (int)(lval % 10000);
		
		if(yi > 0) {
			convertNumberPart(yi, buffer, numbers, units).append("亿");
		}
		if(wan > 0) {
			if(yi > 0 && wan < 1000) {
				buffer.append("零");
			}
			convertNumberPart(wan, buffer, numbers, units).append("万");
		}
		if(remainer > 0) {
			if((yi > 0 || wan > 0) && remainer < 1000) {
				buffer.append("零");
			}
			convertNumberPart(remainer, buffer, numbers, units);
		}
		
		return buffer.toString();
	}
	
	/**
	 * 中文的带小数数字表示，用整数部分和小数部分分别拼接而成。
	 * @param value 要表示的数字。
	 * @param numbers 中文数码数组。
	 * @param units 中文单位数组。
	 * @return 中文的表示结果。
	 */
	public static String convertDecimal(BigDecimal value, final String [] numbers, final String [] units) {
		StringBuilder sb = new StringBuilder();
		if(value.signum() == 0) {
			return "零";
		}
		else if(value.signum() < 0) {
			sb.append("负");
			value = value.abs();
		}
		
		BigInteger decimal = value.toBigInteger();
		BigDecimal fractional = value.remainder(BigDecimal.ONE);
		
		sb.append(convertInteger(decimal, numbers, units));
		if(fractional.signum() == 0) {
			return sb.toString();
		}
		sb.append("点");
		
		String fracstr = fractional.toPlainString().substring(2);
		convertSerial(fracstr, sb, numbers);
		return sb.toString();
	}
	
	/**
	 * 中文整数的一般表示。
	 * @param value
	 * @return
	 */
	public static String toChinese(BigInteger value) {
		return convertInteger(value, CHINESE_NUMBERS, CHINESE_UNITS);
	}
	
	/**
	 * 中文带小数数字的一般表示。
	 * @param value
	 * @return
	 */
	public static String toChinese(BigDecimal value) {
		return convertDecimal(value, CHINESE_NUMBERS, CHINESE_UNITS);
	}
	
	/**
	 * 中文带小数数字的财务金额表示。小数最多两位（分），不接受负数。
	 * @param value 
	 * @return
	 */
	public static String toChineseAccounting(BigDecimal value) {
		assert value.signum() > 0 : "财务数字应当为正数";
		assert value.scale() <= 2 : "财务数字最多精确到小数点后两位（分）";
		
		StringBuilder sb = new StringBuilder();
		
		BigInteger decimal = value.toBigInteger();
		sb.append(convertInteger(decimal, CHINESE_CAPITAL_NUMBERS, CHINESE_CAPITAL_UNITS));
		sb.append("圆");
		
		BigDecimal fractional = value.remainder(BigDecimal.ONE);
		if(fractional.signum() > 0) {
			int cents = fractional.movePointRight(2).intValue();
			int jiao = cents / 10;
			int fen = cents % 10;
			if (jiao == 0) {
				sb.append("零");
			}
			else {
				sb.append(CHINESE_CAPITAL_NUMBERS[jiao]).append("角");
			}
			if(fen > 0) {
				sb.append(CHINESE_CAPITAL_NUMBERS[fen]).append("分");
			}
		}
		sb.append("整");
		return sb.toString();
	}
}
