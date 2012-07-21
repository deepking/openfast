package org.openfast;

import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.test.OpenFastTestCase;

public class MessageInputStreamTest extends OpenFastTestCase {

	public void testReadMessage() {
		MessageInputStream in = new MessageInputStream(bitStream("11000000 10000100"));
		try {
			in.readMessage();
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.D9_TEMPLATE_NOT_REGISTERED, e.getCode());
		}
	}

}
