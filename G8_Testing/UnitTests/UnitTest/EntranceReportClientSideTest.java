package UnitTest;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import Controllers.IReportsManager;
import Controllers.ReportsControl;
import gui.VisitsReportController;
import javafx.scene.chart.XYChart;
import logic.VisitReport;

@SuppressWarnings("unchecked")
public class EntranceReportClientSideTest {

	/**
	 * This is a stub class.
	 * With this class we bypass all the dependencies which are related to ReportsManager.
	 */
	public class stubReportsManager implements IReportsManager {

		@Override
		public ArrayList<VisitReport> countSolosEnterTime(int month) {
			return solosData;
		}

		@Override
		public ArrayList<VisitReport> countSubsEnterTime(int month) {
			return subsData;
		}

		@Override
		public ArrayList<VisitReport> countGroupsEnterTime(int month) {
			return groupsData;
		}

		@Override
		public ArrayList<VisitReport> countSolosEnterTimeWithDays(int month, String day) {
			return solosData;
		}

		@Override
		public ArrayList<VisitReport> countSubsEnterTimeWithDays(int month, String day) {
			return subsData;
		}

		@Override
		public ArrayList<VisitReport> countGroupsEnterTimeWithDays(int month, String day) {
			return groupsData;
		}

	}

	public static ArrayList<VisitReport> subsData;
	public static ArrayList<VisitReport> solosData;
	public static ArrayList<VisitReport> groupsData;
	public static VisitsReportController visitsReportController;
	public static String input;
	public IReportsManager irm;
	public ReportsControl rc;
	private XYChart.Series<Number, Number> data;

	@Before
	public void setUp() throws Exception {
		irm = new stubReportsManager();
		rc = new ReportsControl(irm);

		subsData = new ArrayList<VisitReport>(
				Arrays.asList(new VisitReport(10, "08:00:00"), new VisitReport(5, "10:00:00")));
		solosData = new ArrayList<VisitReport>(
				Arrays.asList(new VisitReport(7, "09:00:00"), new VisitReport(6, "16:00:00")));
		groupsData = new ArrayList<VisitReport>(
				Arrays.asList(new VisitReport(9, "11:00:00"), new VisitReport(12, "13:00:00")));
		visitsReportController = new VisitsReportController();
		input = "Show whole month";

	}

	/**
	 * GROUP TEST
	 * This test checks size of group data for whole month.
	 * 
	 * input: groupsData = ((9, "11:00:00"),(12, "13:00:00")), input = "Show whole month"
	 * expected result: series with size 2
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkSizeGroupDataAllMonthTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceGroupData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series5");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the size of the series */
		int dataSize = data.getData().size();
		assertEquals(groupsData.size(), dataSize);

	}

	/**
	 * GROUP TEST
	 * This test checks X and Y values of group data for whole month.
	 * 
	 * input: groupsData = ((9, "11:00:00"),(12, "13:00:00")), input = "Show whole month"
	 * expected result:
	 * 
	 * Y values of the series:
	 * groupsData.get(0).getSum()
	 * groupsData.get(1).getSum()
	 * 
	 * X values of the series:
	 * 11.0
	 * 13.0
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkXYValuesGroupDataAllMonthTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceGroupData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series5");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the Y values of the series */
		assertEquals(groupsData.get(0).getSum(), data.getData().get(0).getYValue());
		assertEquals(groupsData.get(1).getSum(), data.getData().get(1).getYValue());

		/* Check the X values of the series */
		assertEquals(11.0, data.getData().get(0).getXValue());
		assertEquals(13.0, data.getData().get(1).getXValue());

	}

	/**
	 * SOLOS TEST
	 * This test checks size of solo data for whole month.
	 * 
	 * input: solosData =((7, "09:00:00"),(6, "16:00:00")), input = "Show whole month"
	 * expected result: series with size 2
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkSizeSolosDataAllMonthTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSolosData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series1");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the size of the series */
		int dataSize = data.getData().size();
		assertEquals(solosData.size(), dataSize);

	}

	/**
	 * SOLOS TEST
	 * This test checks X and Y values of solo data for whole month.
	 * 
	 * input: solosData =((7, "09:00:00"),(6, "16:00:00")), input = "Show whole month"
	 * expected result:
	 * 
	 * Y values of the series:
	 * solosData.get(0).getSum()
	 * solosData.get(1).getSum()
	 * 
	 * X values of the series:
	 * 9.0
	 * 16.0
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkXYValuesSolosDataAllMonthTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSolosData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series1");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the Y values of the series */
		assertEquals(solosData.get(0).getSum(), data.getData().get(0).getYValue());
		assertEquals(solosData.get(1).getSum(), data.getData().get(1).getYValue());

		/* Check the X values of the series */
		assertEquals(9.0, data.getData().get(0).getXValue());
		assertEquals(16.0, data.getData().get(1).getXValue());

	}

	/**
	 * SUBS TEST
	 * This test checks size of subscriber data for whole month.
	 * 
	 * input: subsData = ((10, "08:00:00"),(5, "10:00:00")), input = "Show whole month"
	 * expected result: series with size 2
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkSizeSubsDataAllMonthTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSubscribersData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series3");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the size of the series */
		int dataSize = data.getData().size();
		assertEquals(subsData.size(), dataSize);

	}

	/**
	 * SUBS TEST
	 * This test checks X and Y values of subscriber data for whole month.
	 * 
	 * input: subsData = ((10, "08:00:00"),(5, "10:00:00")), input = "Show whole month"
	 * expected result:
	 * 
	 * Y values of the series:
	 * subsData.get(0).getSum()
	 * subsData.get(1).getSum()
	 * 
	 * X values of the series:
	 * 8.0
	 * 10.0
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkXYValuesSubsDataAllMonthTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSubscribersData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series3");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the Y values of the series */
		assertEquals(subsData.get(0).getSum(), data.getData().get(0).getYValue());
		assertEquals(subsData.get(1).getSum(), data.getData().get(1).getYValue());

		/* Check the X values of the series */
		assertEquals(8.0, data.getData().get(0).getXValue());
		assertEquals(10.0, data.getData().get(1).getXValue());

	}

	/**
	 * GROUP TEST
	 * This test size of group data for specific day.
	 * 
	 * input: groupsData = ((9, "11:00:00"),(12, "13:00:00")), input = "4"
	 * expected result: series with size 0
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkSizeGroupDataSpecificDayNoReturendValuesTest() throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, NoSuchFieldException, SecurityException {

		groupsData = new ArrayList<VisitReport>();
		input = "4";

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceGroupData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series5");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the size of the series */
		int dataSize = data.getData().size();

		assertEquals(groupsData.size(), dataSize);

	}

	/**
	 * GROUP TEST
	 * This test checks X and Y values of group data for specific day.
	 * 
	 * input: groupsData = ((9, "11:00:00"),(12, "13:00:00")), input = "4"
	 * expected result:
	 * 
	 * Y values of the series:
	 * groupsData.get(0).getSum()
	 * groupsData.get(1).getSum()
	 * 
	 * X values of the series:
	 * 11.0
	 * 13.0
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkXYValuesGroupDataSpecificDayNoReturendValuesTest() throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, NoSuchFieldException, SecurityException {
		input = "4";

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceGroupData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series5");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the Y values of the series */
		assertEquals(groupsData.get(0).getSum(), data.getData().get(0).getYValue());
		assertEquals(groupsData.get(1).getSum(), data.getData().get(1).getYValue());

		/* Check the X values of the series */
		assertEquals(11.0, data.getData().get(0).getXValue());
		assertEquals(13.0, data.getData().get(1).getXValue());

	}

	/**
	 * SOLOS TEST
	 * This test size of solo data for specific day.
	 * 
	 * input: solosData =((7, "09:00:00"),(6, "16:00:00")), input = "4"
	 * expected result: series with size 2
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkSizeSolosDataSpecificDayTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		input = "4";
		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSolosData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series1");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the size of the series */
		int dataSize = data.getData().size();
		assertEquals(solosData.size(), dataSize);
	}

	/**
	 * SOLOS TEST
	 * This test checks X and Y values of solo data for specific day.
	 * 
	 * input: solosData =((7, "09:00:00"),(6, "16:00:00")), input = "4"
	 * expected result:
	 * 
	 * Y values of the series:
	 * solosData.get(0).getSum()
	 * solosData.get(1).getSum()
	 * 
	 * X values of the series:
	 * 9.0
	 * 16.0
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkXYValuesSolosDataSpecificDayTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		input = "4";

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSolosData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series1");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the Y values of the series */
		assertEquals(solosData.get(0).getSum(), data.getData().get(0).getYValue());
		assertEquals(solosData.get(1).getSum(), data.getData().get(1).getYValue());

		/* Check the X values of the series */
		assertEquals(9.0, data.getData().get(0).getXValue());
		assertEquals(16.0, data.getData().get(1).getXValue());

	}

	/**
	 * SUBS TEST
	 * This test size of subscriber data for specific day.
	 * 
	 * input: subsData = ((10, "08:00:00"),(5, "10:00:00")), input = "4"
	 * expected result: series with size 2
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkSizeSubsDataSpecificDayTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		input = "4";

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSubscribersData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series3");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the size of the series */
		int dataSize = data.getData().size();
		assertEquals(subsData.size(), dataSize);

	}

	/**
	 * SUBS TEST
	 * This test checks X and Y values of subscriber data for specific day.
	 * 
	 * input: subsData = ((10, "08:00:00"),(5, "10:00:00")), input = "4"
	 * expected result:
	 * 
	 * Y values of the series:
	 * subsData.get(0).getSum()
	 * subsData.get(1).getSum()
	 * 
	 * X values of the series:
	 * 8.0
	 * 10.0
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@Test
	public void checkXYValuesSubsDataSpecificDayTest() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, NoSuchFieldException, SecurityException {

		input = "4";

		Method method = VisitsReportController.class.getDeclaredMethod("loadEntranceSubscribersData", String.class);
		method.setAccessible(true);
		method.invoke(visitsReportController, input);

		Field actualData = VisitsReportController.class.getDeclaredField("series3");
		actualData.setAccessible(true);
		data = (XYChart.Series<Number, Number>) actualData.get(visitsReportController);

		/* Check the Y values of the series */
		assertEquals(subsData.get(0).getSum(), data.getData().get(0).getYValue());
		assertEquals(subsData.get(1).getSum(), data.getData().get(1).getYValue());

		/* Check the X values of the series */
		assertEquals(8.0, data.getData().get(0).getXValue());
		assertEquals(10.0, data.getData().get(1).getXValue());

	}

}