import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Run OrgNow from here.
 *
 * @author Pavi Lee
 * @version September 5, 2020
 */
public class Main {

    public static final PageManager PAGE_MANAGER;

    static {
        PAGE_MANAGER = new PageManager(200, 50, 600, 600);
    }

    public static void main(String[] args) throws IOException,
            GeneralSecurityException {
        PAGE_MANAGER.addPage(new MainPage());
    }

}
