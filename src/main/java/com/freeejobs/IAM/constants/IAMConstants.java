package com.freeejobs.IAM.constants;

public class IAMConstants {

	private IAMConstants() {}

    public static final class LOGIN {
        private LOGIN() {}

        public static final int STATUS_FAIL = 0;
        public static final int STATUS_SUCCESS = 1;
        public static final int STATUS_ACTIVE_SESSION = 2;
        public static final int STATUS_LOCKED = 3;
        public static final int FAIL_ATTEMPT = 3;
        public static final int DEFAULT_ATTEMPT = 0;
        public static final int SESSION_DURATION = 5;

    }

    public static final class USER {
        private USER() {}

        public static final int USER_ROLE_ADMIN = 2;
        public static final int USER_ROLE_REGULAR = 1;

    }
    
    public static final class RESETIND {
        private RESETIND() {}

        public static final int TO_RESET = 1;
        public static final int TO_FORCE_PW_CHANGE = 2;
        public static final int TO_NULL = 0;

    }

}
