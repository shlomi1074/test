package UnitTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AutenticationControlClientSideTest.class, AutenticationControlServerSideTest.class,
		EntranceReportClientSideTest.class, EntranceReportServerSide.class })
public class AllTests {

}
