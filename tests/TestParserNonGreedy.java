import org.junit.*;
import static org.junit.Assert.*;

import java.util.HashMap;

import interpreter.Interpreter;
import options.OptId;
import options.Options;

import parser.Parser;
import types.TypedValue;
import lexer.Lexer;
import main.Buffer;
import main.MyException;
import main.OutputSystem;

public class TestParserNonGreedy {
	static Lexer l;
	static Buffer b;
	static Parser p;
	static Interpreter i;
	
	@Before
	public void setUp() throws MyException {
		OutputSystem out = new OutputSystem();
		l = new Lexer();
		Options o = new Options(out);
		o.set(OptId.AUTO_END, true);
		o.set(OptId.GREEDY_FUNC, false);
		MyException.staticInit(o, out);
		b = new Buffer(l, null, null, o, out);
		i = new Interpreter(o, new HashMap<String, TypedValue>(), out);
		p = new Parser(b, i);
	}

	@After
	public void tearDown() throws Exception {
		if (MyException.getErrors() > 0)
			System.err.println("Ошибка на " + b.getLineNum());
		assertTrue(MyException.getErrors() == 0);
	}

	@Ignore // TODO снять после запила функций
	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object() {
		}.getClass().getEnclosingMethod().getName());
		b.setArgs(new String[] { "sin(-pi/2)" });
		p.program();
		assertEquals(-1.0, i.lastResult);
	}

	@Test
	public void test3FactorialFactorial() throws Exception {
		b.setArgs(new String[] { "3!!" });
		p.program();
		assertEquals(720, i.lastResult.getInt());
	}

	@Test
	public void test3FactorialAdd4Factorial() throws Exception {
		b.setArgs(new String[] { "3!+4!" });
		p.program();
		assertEquals(30, i.lastResult.getInt());
	}

	@Test
	public void test1minus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] { "1-3!+4!" });
		p.program();
		assertEquals(19, i.lastResult.getInt());
	}

	@Test
	public void test1plus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] { "1+3!+4!" });
		p.program();
		assertEquals(31, i.lastResult.getInt());
	}

	@Test
	public void test3FactorialMul4Factorial() throws Exception {
		b.setArgs(new String[] { "3!*4!" });
		p.program();
		assertEquals(144, i.lastResult.getInt());
	}

	@Test
	public void test2pow3Factorial() throws Exception {
		b.setArgs(new String[] { "2^3!" }); // 2^(3!)
		p.program();
		assertEquals(64, i.lastResult.getInt());
	}

	@Test
	public void test2pow3FactorialPlus1() throws Exception {
		b.setArgs(new String[] { "2^3!+1" }); // 2^(3!)+1
		p.program();
		assertEquals(65, i.lastResult.getInt());
	}

	@Test
	public void testRightAssociatePower() throws Exception {
		b.setArgs(new String[] { "2^3^4" });
		p.program();
		assertEquals((int)Math.pow(2, Math.pow(3, 4)), i.lastResult.getInt());
	}
}