package vn.com.call;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;

import vn.com.call.model.contact.Contact;

public class favController extends DataBase {
    public favController(Context con) {
        super(con);
    }

    public ArrayList<Contact> getAllDevice() {
        ArrayList<Contact> listDevice = new ArrayList<>();
        try {
            // Trước khi thao tác với file sqlite thì phải mở nó ra
            openDataBase();
            // Lấy ra các bản ghi có trong file qlsv.db trong bộ nhớ trong
            // database.rawQuery() dùng để truy vấn ra dữ liệu sử dụng ngôn ngữ sql
            StringBuffer buffer = new StringBuffer();
            // String selectQuery = "SELECT * FROM devices WHERE roomid = " ;
            String selectQuery = "SELECT * FROM myFav ";

            Cursor cursor = database.rawQuery(selectQuery, null);

            // Lấy các thông tin ra từ trong đối tượng Cursor
            while (cursor.moveToNext()) {
                // Lấy ra giá trị của từng bản ghi
                //int roomid = cursor.getInt(0);

                int contactId = cursor.getInt(0);
                String namecontact = cursor.getString(1);
                String typeFav = cursor.getString(2);
                String phone = cursor.getString(3);
                String image = cursor.getString(4);
                Contact device1 = new Contact(String.valueOf(contactId), typeFav, namecontact,phone,image);
                listDevice.add(device1);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng db lại
            close();
        }
        return listDevice;
    }
   public void deleteitem(Contact contact){

   }
    public boolean insertDevice(Contact contact, String callOrsms,String phonenumber ,String photo) {
        try {
            openDataBase();
            ContentValues values = new ContentValues();

                values.put("id", contact.getId());
                values.put("name", contact.getName());
                values.put("typeFav", callOrsms);
                values.put("phone", phonenumber);
                values.put("image", photo);
                long id = database.insert("myFav", null, values);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return false;
    }

    public void deleteDevice(Contact contact,String callORsms) {
        try {
            openDataBase();
            long id = database.delete("myFav", "id =? and typeFav =? ", new String[]{contact.getId(),callORsms});


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }
    public String checkcallorsms(Contact contact,String callorsms){
        String typeFav = null;

        try {
            // Trước khi thao tác với file sqlite thì phải mở nó ra
            openDataBase();
            // Lấy ra các bản ghi có trong file qlsv.db trong bộ nhớ trong
            // database.rawQuery() dùng để truy vấn ra dữ liệu sử dụng ngôn ngữ sql
            StringBuffer buffer = new StringBuffer();
            // String selectQuery = "SELECT * FROM devices WHERE roomid = " ;
            String selectQuery = "SELECT * FROM myFav WHERE id =  "+contact.getId()+" "+"and typeFav = ";

                buffer.append(selectQuery);
            buffer.append('"');
                buffer.append(callorsms);
                buffer.append('"');
            Cursor cursor = database.rawQuery(buffer.toString(), null);
            // Lấy các thông tin ra từ trong đối tượng Cursor
            while (cursor.moveToNext()) {
                // Lấy ra giá trị của từng bản ghi
                //int roomid = cursor.getInt(0);
                 typeFav = cursor.getString(2);
                Log.d("fdffdffd",typeFav);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng db lại
            close();
        }
        return  typeFav;
    }

}
