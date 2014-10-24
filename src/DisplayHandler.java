import java.awt.Dimension;
import org.apache.commons.codec.binary.Base64;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.apache.camel.converter.stream.CachedOutputStream;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.krysalis.barcode4j.output.bitmap.ImageIOBitmapEncoder;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

    @WebSocket
    public class DisplayHandler {

    	public BufferedImage myImage()
    	{
    		Robot r = null;
			try {
				r = new Robot();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle rect = new Rectangle(0,0,size.width, size.height);
			System.gc();
			return r.createScreenCapture(rect);
    	}
		
        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        }

        @OnWebSocketError
        public void onError(Throwable t) {
            System.out.println("Error: " + t.getMessage());
        }

        @OnWebSocketConnect
        public void onConnect(Session session) {
            System.out.println("Connect: " + session.getRemoteAddress().getAddress());
            while( true ) {
	            try {
	            	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            	BufferedImage img = myImage();
					int width = (int)(img.getWidth()*.6);
					int height = (int)(img.getHeight()*.6);
					java.awt.image.BufferedImage scaled = new java.awt.image.BufferedImage(width/3, height/3, img.getType()); 
					Graphics2D graphics2D = scaled.createGraphics();
					AffineTransform xform = AffineTransform.getScaleInstance(.6, .6);
					graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					graphics2D.drawImage(img, xform, null);
					graphics2D.dispose();

	            	ImageIO.write(scaled, "png", outputStream);
	            	String base2 = Base64.encodeBase64String( outputStream.toByteArray() );
	            	session.getRemote().sendString( base2 );
					Thread.sleep(120);
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            }
	            
            }
        }

        @OnWebSocketMessage
        public void onMessage(String message) {
            System.out.println("Yo Mama soooo fattttt: ");// + message);
        }
}