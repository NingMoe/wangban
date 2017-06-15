
import org.tanukisoftware.wrapper.WrapperSimpleApp;

public class ICityService extends WrapperSimpleApp {
  protected ICityService(String[] strings) {
    super(new String[]{strings[0],"productModel"});
  }

  public static void main(String args[]) {
    new ICityService(args);
  }
}
