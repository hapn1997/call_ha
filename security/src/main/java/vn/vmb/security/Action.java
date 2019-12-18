package vn.vmb.security;

/**
 * Created by ngson on 21/09/2017.
 */

public enum  Action {
    UNLOCK {
        @Override
        public String toString() {
            return "unlock";
        }
    }, CREATE_PASSWORD {
        @Override
        public String toString() {
            return "create_password";
        }
    }
}
