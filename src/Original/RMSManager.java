package Original;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.microedition.rms.*;
import java.util.*;
import java.io.*;
import javax.microedition.lcdui.List;
/**
 *
 * @author Thach Vu
 */
 public class RMSManager {
  private String rmsName;
  private RecordStore recordStore;
  private  Vector recordliststore;
  private String tenmap;
  private byte[][]matran;
  private int dong,cot,soitem,sobom,sonpc;
  public RMSManager(String rmsName) {
    this.rmsName = rmsName;
  }
    //mo ket noi rms
    public void open() throws Exception {
        try {
            recordStore = RecordStore.openRecordStore(this.rmsName,true);
    
            } catch (Exception e) {
        throw new Exception(this.rmsName+"::open::"+e);}
    }
    //dong ket noi rms
    public void close() throws Exception {
        if (recordStore != null) {
            try {
            recordStore.closeRecordStore();
        } catch(Exception e) {
            throw new Exception(this.rmsName+"::close::"+e); }
        }
    }
     //cap nhat map xuong rms
    public void updateData(byte[] data ) throws Exception {
        try {
            if (recordStore.getNumRecords() > 0) {//neu co roi chi cap nhat
            recordStore.setRecord(1,data, 0, data.length);
        } else {
          recordStore.addRecord(data, 0,data.length);//neu chua co thi tao moi
        }
        } catch(Exception e) {
          throw new Exception(this.getRMSName() + "::updateData::" + e);
        }
    }

    //doc 1 chuoi tu rms
    public byte[] readRMS(int recordID) {
    	byte[] str=null;
            try {
                if (recordStore.getNumRecords() > 0) {
                       str = recordStore.getRecord(recordID);
                 }
              
            } catch (InvalidRecordIDException ex) {
                ex.printStackTrace();
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
          
    	return str;
    }
    
    //dua 1 chuoi chuyen thanh mang byte
    public  byte[]  toByteArray(String str) throws IOException{
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        DataOutputStream dOut = new DataOutputStream(bOut);

        dOut.writeUTF(str);

        dOut.close();

        return bOut.toByteArray();

    }
     //dua 1 so chuyen thanh mang byte
    public  byte[]  toByteArray(int str) throws IOException{
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        DataOutputStream dOut = new DataOutputStream(bOut);

        dOut.writeInt(str);

        dOut.close();

        return bOut.toByteArray();

    }
     //dua 1 long chuyen thanh mang byte
    public  byte[]  toByteArray(long str) throws IOException{
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        DataOutputStream dOut = new DataOutputStream(bOut);

        dOut.writeLong(str);

        dOut.close();

        return bOut.toByteArray();

    }
    //dua 1 mang byte chuyen thanh 1 chuoi
    public  String fromByteArray(byte[] data) throws IOException{
        String str;
        ByteArrayInputStream bIn = new ByteArrayInputStream(data);

        DataInputStream dIn = new DataInputStream(bIn);

        str=dIn.readUTF();

        dIn.close();

        return str;

    }
    //lay record
    public RecordStore getRecordStore() {
        return this.recordStore;
    }
    //lay rms name
    public String getRMSName() {
        return this.rmsName;
    }

    //convert mang byte ve du lieu ban dau
    public void convertdata(byte[] data)
    {
        try {
            ByteArrayInputStream bIn = new ByteArrayInputStream(data);
            DataInputStream dIn = new DataInputStream(bIn);
            tenmap = dIn.readUTF();
            dong=dIn.readInt();
            cot=dIn.readInt();
            matran=new byte[dong][cot];
            for(int i=0;i<dong;i++)
            {
                 for(int j=0;j<cot;j++)
                     matran[i][j]=dIn.readByte();
            }
           
            dIn.close();
           
          
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //cong don mang byte lai voi nhau
    public byte[] addMangbyte(byte[] nguon,byte[] them)
    {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(bOut);
        try {

            dOut.write(nguon, 0, nguon.length);
            dOut.write(them, 0, them.length);


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally
        {
            try {
                dOut.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bOut.toByteArray();
    }
    //don matran thanh mang byte
    public byte[] convert_matran(byte[][] matran,int dong ,int cot )
    {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(bOut);
            try {
                for(int i=0;i<dong;i++)
                dOut.write(matran[i], 0,matran[i].length);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                try {
                    dOut.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        return bOut.toByteArray();
    }

  
  


}

