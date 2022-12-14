import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DiskManager {
    private static List<Fichier> listF = new ArrayList<>();
    private static String path_name = DBParams.DBPath + "/save.data";
    private static int currentCountAllocPages=0;
    private static File save = new File(DBParams.DBPath+File.separator+"save.data");
    private static File savePNA = new File(DBParams.DBPath+File.separator+"savePNA.data");
    private static File savePA = new File(DBParams.DBPath+File.separator+"savePA.data");
    private static ArrayList<PageId> listeDePagesNonAlloue = new ArrayList<PageId>();
    private static ArrayList<PageId> listeDePagesAlloue = new ArrayList<PageId>();
    private static DiskManager leDiskManager = new DiskManager();

    private DiskManager() {
    }

    public static DiskManager getLeDiskManager() {
        return leDiskManager;
    }

    public ArrayList<PageId> getListeDePagesNonAlloue() throws IOException{
        if (savePNA.length() != 0)
            inSavePNA();
        return listeDePagesNonAlloue;
    }

    public ArrayList<PageId> getListeDePagesAlloue() throws IOException{
        if (savePA.length() != 0)
            inSavePA();
        return listeDePagesAlloue;
    }
    public PageId allocPage() throws IOException{
        rempTabs();
        //System.out.println(listeDePagesAlloue.size());
        Fichier nomFDisp = fIsDisp();
        if (listF.isEmpty()) {
            nomFDisp = creerF();
        } else if (nomFDisp == null) {
            nomFDisp = creerF();
        }
        //System.out.println(nomFDisp.getNumF());
        PageId pageId = null;
        if (listeDePagesNonAlloue.isEmpty()) {
            //System.out.println("HEYYYY3");
            pageId = new PageId(nomFDisp.getNumF(), nomFDisp.getSize());
            ecriture(nomFDisp, nomFDisp.getSize());
            listeDePagesAlloue.add(pageId);
        }
        else
        {
            //System.out.println("HEYYYY4");
            ecriture(nomFDisp, listeDePagesNonAlloue.get(0).getFileIdx());
            pageId = new PageId(nomFDisp.getNumF(), listeDePagesNonAlloue.get(0).getPageIdx());
            listeDePagesAlloue.add(pageId);
        }
        outList();
        outSavePA();
        if (!listeDePagesNonAlloue.isEmpty()) {
            listeDePagesNonAlloue.remove(0);
            outSavePNA();
        }
        return pageId;
    }

    public void ReadPage (PageId pageId) throws IOException {
        if (savePA.length() != 0)
            inSavePA();
        if (verifPageId(pageId)) {
            String fichier = Integer.toString(pageId.getFileIdx());//Transformation du file name en String
            try {
                String n = "F" + fichier + ".bdda";
                File file = new File(DBParams.DBPath + "/" + n);
                RandomAccessFile randomaccessfile = new RandomAccessFile(file, "r");
                randomaccessfile.seek((long) pageId.getPageIdx() * DBParams.pageSize);
                byte[] tableaudebyte = new byte[DBParams.pageSize];
                randomaccessfile.read(tableaudebyte);
                ByteBuffer buff = ByteBuffer.wrap(tableaudebyte);
                buff.rewind();
                System.out.println(buff.toString());
                randomaccessfile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
            System.out.println("ERREUR : La page ("+pageId.getFileIdx()+", "+ pageId.getPageIdx()+") n'est pas encore allou??e");
    }

    private Boolean verifPageId(PageId pageId){
        boolean bul = false;
        int i = 0;
        for (PageId pageId1 : listeDePagesAlloue)
        {
            if (pageId1.compareTo(pageId)) {
                bul = true;
                break;
            }
            i++;
        }
        return bul;
    }

    public void WritePage (PageId pageId, ByteBuffer buff) throws IOException {
        if (savePA.length() != 0)
            inSavePA();
        if (verifPageId(pageId)) {
            String fichier = Integer.toString(pageId.getFileIdx());//Transformation du file name en String
            try {
                String n = "F" + fichier + ".bdda";
                File file = new File(DBParams.DBPath + "/" + n);
                RandomAccessFile randomaccessfile = new RandomAccessFile(file, "rw");
                randomaccessfile.seek((long) pageId.getPageIdx() *DBParams.pageSize);
                randomaccessfile.write(buff.array());


                randomaccessfile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
            System.out.println("ERREUR : La page ("+pageId.getFileIdx()+", "+ pageId.getPageIdx()+") n'est pas encore allou??e");
    }

    public void writeAtEnd(PageId pageId, ByteBuffer buff) throws IOException {
        if (savePA.length() != 0)
            inSavePA();
        if (verifPageId(pageId)) {
            String fichier = Integer.toString(pageId.getFileIdx());//Transformation du file name en String
            try {
                String n = "F" + fichier + ".bdda";
                File file = new File(DBParams.DBPath + "/" + n);
                RandomAccessFile randomaccessfile = new RandomAccessFile(file, "rw");
                randomaccessfile.seek(((long) pageId.getPageIdx() *DBParams.pageSize)-8);
                randomaccessfile.write(buff.array());


                randomaccessfile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
            System.out.println("ERREUR : La page ("+pageId.getFileIdx()+", "+ pageId.getPageIdx()+") n'est pas encore allou??e");
    }

    public void DeallocPage (PageId pageId ) throws IOException {
        rempTabs();
        System.out.println(pageId.getFileIdx()+", "+ pageId.getPageIdx());
        System.out.println(listeDePagesAlloue.size());
        boolean bul = false;
        int i = 0;
        for (PageId pageId1 : listeDePagesAlloue)
        {
            if (pageId1.compareTo(pageId)) {
                //bul = true;
                break;
            }
            i++;
        }
        System.out.println(bul);
        if (verifPageId(pageId)) {
            listeDePagesAlloue.remove(i);
            listeDePagesNonAlloue.add(pageId);
            outSavePNA();
            outSavePA();
            System.out.println("Page ("+pageId.getFileIdx()+", "+ pageId.getPageIdx()+") d??sallou??e avec succ??s.");
        }
        else
            System.out.println("ERREUR : La page ("+pageId.getFileIdx()+", "+ pageId.getPageIdx()+") n'est pas encore allou??e");
    }
    private void ecriture(Fichier f, int pos) throws IOException {
        File file = new File(DBParams.DBPath+"/"+f.getFile());
        ByteBuffer buff = ByteBuffer.allocate(DBParams.pageSize);
        RandomAccessFile randomaccessfile = new RandomAccessFile(file, "rw");
        randomaccessfile.seek((long) pos *DBParams.pageSize);
        randomaccessfile.write(buff.array());
        randomaccessfile.close();

        if (listeDePagesNonAlloue.isEmpty()) {
            f.setSize(f.getSize() + 1);
            System.out.println("Whyyyyyy");
        }
        currentCountAllocPages++;
        System.out.println(currentCountAllocPages);
    }

    private Fichier creerF() throws IOException {
        StringBuffer nomF = new StringBuffer();
        int i1 = 0;
        if (listF.isEmpty()) {
            nomF.append("F").append(i1).append(".bdda");
        }
        else
        {
            for (Fichier fichier : listF)
                i1++;
            nomF.append("F").append(i1).append(".bdda");
        }
        Fichier fichier = new Fichier(nomF.toString());
        fichier.setNumF(i1);
        File myFile = new File(DBParams.DBPath+"/"+nomF);

        if (myFile.createNewFile()){
        }
        listF.add(fichier);
        return fichier;
    }


    private Fichier fIsDisp() {
        if (listF.isEmpty())
            return null;
        if (!listeDePagesNonAlloue.isEmpty())
        {
           return listF.get(listeDePagesNonAlloue.get(0).getFileIdx());
        }
        else {
            for (Fichier fichier : listF) {
                if (fichier.getSize() < DBParams.maxPagesPerFile) {
                    return fichier;
                }
            }
            return null;
        }
    }

    private void outList() throws IOException {
        OutputStream os = new FileOutputStream(save);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        for (Fichier fichier : listF)
        {
            oos.writeObject(fichier);
        }
        oos.close();
    }

    private void rempTabs() throws IOException {
        if (save.length() != 0)
            inListF();
        if (savePNA.length() != 0)
            inSavePNA();
        if (savePA.length() != 0)
            inSavePA();
    }
    private void inListF() throws IOException {
        InputStream is = new FileInputStream(save);
        ObjectInputStream ois = new ObjectInputStream(is);
        listF.clear();
        boolean restaL = false;
        try {
            restaL = true;
            while (restaL) {
                try {
                    Fichier nF = (Fichier) ois.readObject();
                    listF.add(nF);
                    restaL = true;
                } catch (Exception e) {
                    restaL = false;
                }
            }
            ois.close();
        } catch (Exception e) {
            restaL = false;
        }
    }

    private void inSavePA() throws IOException {
        outPut(savePA, listeDePagesAlloue);
    }
    private void outSavePA() throws IOException {
        OutputStream os = new FileOutputStream(savePA);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        for (PageId pageId : listeDePagesAlloue)
            oos.writeObject(pageId);
        oos.close();
    }
    private void outSavePNA() throws IOException {
        OutputStream os = new FileOutputStream(savePNA);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        for (PageId pageId : listeDePagesNonAlloue)
            oos.writeObject(pageId);
        oos.close();
    }

    private void inSavePNA() throws IOException {
        outPut(savePNA, listeDePagesNonAlloue);
    }

    private void outPut(File savePNA, ArrayList<PageId> listeDePagesNonAlloue) throws IOException {
        InputStream is = new FileInputStream(savePNA);
        ObjectInputStream ois = new ObjectInputStream(is);
        listeDePagesNonAlloue.clear();
        boolean restaL = false;
        try {
            restaL = true;
            while (restaL) {
                try {
                    PageId nF = (PageId) ois.readObject();
                    listeDePagesNonAlloue.add(nF);
                    restaL = true;
                } catch (Exception e) {
                    restaL = false;
                }
            }
            ois.close();
        } catch (Exception e) {
            restaL = false;
        }
    }

    public int getCurrentCountAllocPages() throws IOException {
        if (savePA.length() != 0)
            inSavePA();
        for (PageId pageId : listeDePagesAlloue)
            System.out.println(pageId.getFileIdx()+", "+ pageId.getPageIdx());
        return listeDePagesAlloue.size();
    }
}
