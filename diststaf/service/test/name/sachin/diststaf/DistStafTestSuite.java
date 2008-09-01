package name.sachin.diststaf;

import name.sachin.diststaf.service.wrapper.FileSystemTest;
import name.sachin.diststaf.service.wrapper.ServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ServiceTest.class, FileSystemTest.class })
public class DistStafTestSuite {

}
