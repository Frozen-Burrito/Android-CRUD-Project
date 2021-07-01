package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public final class Person extends Entity {

    public static final String TABLE_NAME = "persona";
    public static final String TAG = "PersonEntity";

    public static final String COLUMN_NAME = "nombre";
    public static final String COLUMN_LASTNAME = "apellido";
    public static final String COLUMN_PHONE = "celular";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_GARDEN = "id_jardin";
    public static final String COLUMN_TAG = "id_rol";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_NAME,
        COLUMN_LASTNAME,
        COLUMN_PHONE,
        COLUMN_EMAIL,
        COLUMN_GARDEN,
        COLUMN_TAG
    };

    private long mId;
    private String mName;
    private String mLastname;
    private String mPhone;
    private String mEmail;
    private long mGarden;
    private long mTag;

    private List<String> mErrors;

    public Person() {}

    private Person(long id, String name, String lastname, String phone, String email, long gardenId, long tagId) {
        this.mId = id;
        this.mName = name;
        this.mLastname = lastname;
        this.mEmail = email;
        this.mPhone = phone;
        this.mGarden = gardenId;
        this.mTag = tagId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Person.COLUMN_NAME, mName);
        values.put(Person.COLUMN_LASTNAME, mLastname);
        values.put(Person.COLUMN_PHONE, mPhone);
        values.put(Person.COLUMN_EMAIL, mEmail);
        values.put(Person.COLUMN_GARDEN, mGarden);
        values.put(Person.COLUMN_TAG, mTag);

        return values;
    }

    public boolean validar() {
        this.mErrors = new ArrayList<>();
        if (this.mName.isEmpty()) mErrors.add("El nombre está vacío");
        if (this.mLastname.equals("")) mErrors.add("El apellido está vacío");
        if (this.mEmail.equals("")) mErrors.add("El email está vacío");
        if (this.mPhone.equals("")) mErrors.add("El número celular está vacío");
        if (this.mTag < 0) mErrors.add("Crea una etiqueta para la persona");

        return this.mErrors.size() == 0;
    }

    public static Person fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Person person = new Person(
            c.getLong(0),
            c.getString(1),
            c.getString(2),
            c.getString(3),
            c.getString(4),
            c.getLong(5),
            c.getLong(6)
        );

        c.close();
        return person;
    }

    public static List<Person> manyFromCursor(Cursor c) {
        List<Person> personList = new ArrayList<Person>();

        while (c.moveToNext()) {
            Person person = new Person(
                c.getLong(0),
                c.getString(1),
                c.getString(2),
                c.getString(3),
                c.getString(4),
                c.getLong(5),
                c.getLong(6)
            );
            personList.add(person);
        }

        c.close();
        return personList;
    }

    //region Get/Set
    public long getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLastname() {
        return mLastname;
    }

    public void setLastname(String lastname) {
        mLastname = lastname;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public long getGarden() {
        return mGarden;
    }

    public void setGarden(long garden) {
        mGarden = garden;
    }

    public long getTag() {
        return mTag;
    }

    public void setTag(long tag) {
        mTag = tag;
    }

    public List<String> getErrors() {
        return mErrors;
    }
    //endregion
}