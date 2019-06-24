/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package confianceoffline;

/**
 *
 * @author User
 */
public class Result {


        private String qrcode_path;
        private String signed_file;

        public Result() {
        }

        public String getQrcode_path() {
            return qrcode_path;
        }

        public void setQrcode_path(String qrcode_path) {
            this.qrcode_path = qrcode_path;
        }

        public String getSigned_file() {
            return signed_file;
        }

        public void setSigned_file(String signed_file) {
            this.signed_file = signed_file;
        }

        public String toString() {
            return "Result [ qrcode_path: " + qrcode_path + ", signed_file: " + signed_file + " ]";
        }
    
}
