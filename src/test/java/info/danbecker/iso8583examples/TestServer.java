package info.danbecker.iso8583examples;

import java.io.*;

import org.jpos.iso.*;
import org.jpos.util.*;
import org.jpos.iso.channel.*;
import org.jpos.iso.packager.*;

/**
 * Example of simple ISO-8583 server.
 * Listens and replies to ISO messages
 * <p>
 * Telnet to port 8000 and send an XML-formatted ISO-8583 message. 
 * (telnet localhost 8000)
 * You’ll get a response, with a result code "00" (field 39), e.g.:
 * <code>
 * (you type)
 * <isomsg>
 *    <field id="0" value="0800"/>
 *    <field id="3" value="333333"/>
 * </isomsg>
 * (and you should receive)
 * <isomsg direction="outgoing">
 *    <!-- org.jpos.iso.packager.XMLPackager -->
 *    <field id="0" value="0810"/>
 *    <field id="3" value="333333"/>
 *    <field id="39" value="00"/>
 * </isomsg>
 * </code>
 * (telnet ^] quit)
 */
public class TestServer implements ISORequestListener {
	public static final org.slf4j.Logger jLogger = 
		org.slf4j.LoggerFactory.getLogger(TestServer.class);

	public boolean process(ISOSource source, ISOMsg msg) {
		try {
			// jLogger.info( "Received msg=" + msg );
			msg.setResponseMTI();
			msg.set(39, "00");
			source.send(msg);
		} catch (ISOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		Logger logger = new Logger(); // org.jpos.util
		logger.addListener(new SimpleLogListener(System.out));
		ServerChannel channel = new XMLChannel(new XMLPackager());
		((LogSource) channel).setLogger(logger, "channel");
		ISOServer server = new ISOServer(8000, channel, null);
		server.setLogger(logger, "server");
		server.addISORequestListener(new TestServer());
		new Thread(server).start();
	}
}
