package www.leg.com.sharelib.exception;

/**
 * Created by liushenghan on 2017/2/28.
 */

public class ThirdPartExp extends RuntimeException {

    public ThirdPartExp() {
        super();
    }

    public ThirdPartExp(String detailMessage) {
        super(detailMessage);
    }

    public ThirdPartExp(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ThirdPartExp(Throwable throwable) {
        super(throwable);
    }

}
