package Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Wentao on 2015/12/27.
 * <p>
 * Storage one name card data and implements Parcelable interface to realize transport NameCard by intent
 */
public class NameCard implements Parcelable, Comparable<NameCard> {

    private int id;
    private String name;
    private String job;
    private String phone;
    private String email;
    private String address;
    private String photoPath;

    public NameCard() {
        this.id = -1;
        this.address = null;
        this.email = null;
        this.job = null;
        this.name = null;
        this.phone = null;
        this.photoPath = null;
    }

    ;

    public NameCard(String name, String phone, String job, String email, String address) {
        this.id = -1;
        this.address = address;
        this.email = email;
        this.job = job;
        this.name = name;
        this.phone = phone;
        this.photoPath = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setPhotoPathFromFile(File file) {
        this.photoPath = file.getName();
    }

    /**
     * return a different instance but has the same contents
     *
     * @return return a different instance but has the same contents
     */
    public NameCard getCopy() {
        NameCard copyCard = new NameCard(this.name, this.phone, this.job, this.email, this.address);
        copyCard.setId(this.id);
        copyCard.setPhotoPath(this.photoPath);
        return copyCard;
    }

    public void setValue(NameCard nameCard) {
        this.id = nameCard.getId();
        this.photoPath = nameCard.getPhotoPath();
        this.name = nameCard.getName();
        this.job = nameCard.getJob();
        this.phone = nameCard.getPhone();
        this.email = nameCard.getEmail();
        this.address = nameCard.getAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == this.getClass()) {
            NameCard n = (NameCard) o;
            if (this.id==n.getId()&&(this.name+this.phone+this.email+this.job+this.address).equals(n.getName()+n.getPhone()+n.getEmail()+n.getJob()+n.getAddress())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //该方法将类的数据[按顺序]写入外部提供的Parcel中
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(job);
        parcel.writeString(email);
        parcel.writeString(address);
        parcel.writeString(photoPath);
    }

    public static final Creator<NameCard> CREATOR = new Creator<NameCard>() {
        @Override
        public NameCard createFromParcel(Parcel source) {
            //从Parcel容器中[按顺序]读取传递数据值，封装成Parcelable对象返回逻辑层。
            NameCard nameCard = new NameCard();
            nameCard.setId(source.readInt());
            nameCard.setName(source.readString());
            nameCard.setPhone(source.readString());
            nameCard.setJob(source.readString());
            nameCard.setEmail(source.readString());
            nameCard.setAddress(source.readString());
            nameCard.setPhotoPath(source.readString());
            return nameCard;
        }

        @Override
        public NameCard[] newArray(int size) {
            return new NameCard[size];
        }
    };

    @Override
    public int compareTo(NameCard another) {
        return TextHelper.replaceChinese(this.name).toLowerCase().compareTo(TextHelper.replaceChinese(another.name).toLowerCase());
    }
}
