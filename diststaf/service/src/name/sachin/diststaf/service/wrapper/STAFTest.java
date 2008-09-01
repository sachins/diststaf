package name.sachin.diststaf.service.wrapper;

import com.ibm.staf.*;

public class STAFTest {
	public static void main(String argv[]) {
		try {
			STAFHandle handle = new STAFHandle("MyApplication");

			try {
				System.out.println("My handle is: " + handle.getHandle());
				String result1 = handle.submit("LOCAL", "PING", "PING");
				System.out.println("PING Result: " + result1);
				String result2 = handle.submit(STAFHandle.ReqQueueRetain,
						"LOCAL", "PING", "PING");
				System.out.println("PING Request number: " + result2);
				STAFResult result3 = handle.submit2("LOCAL", "ECHO",
						"ECHO Hello");
				System.out.println("ECHO Result: " + result3.result);
				STAFResult result4 = handle.submit2(STAFHandle.ReqRetain,
						"LOCAL", "ECHO", "ECHO Hello");
				System.out.println("Asynchronous ECHO Request number: "
						+ result4.result);

				STAFResult result = handle.submit2("local", "handle",
						"create handle name " + "MyStaticHandleName");

				if (result.rc == 0) {
					new STAFHandle(new Integer(result.result).intValue());
				}
			} catch (STAFException e) {
				System.out.println("Error submitting request to STAF, RC: "
						+ e.rc);
				System.out.println(e.getMessage());
			} finally {
				handle.unRegister();
			}
		} catch (STAFException e) {
			System.out.println("Error (un)registering with STAF, RC:" + e.rc);
			System.exit(1);
		}

	} // End of main()

} // End of STAFTest
