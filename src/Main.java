import java.io.IOException;
import java.nio.ByteBuffer;

public class Main
{
    public static void main(String [] args) throws IOException, ClassNotFoundException {
        DBParams.DBPath = args[0];
        DBParams.pageSize = 4096;
        DBParams.maxPagesPerFile = 4;
        DBParams.frameCount = 2;
        //DiskManager.getLeDiskManager().allocPage();
        /*RelationInfo relationInfo = new RelationInfo("Voiture", 2);
        relationInfo.addRelaInfo("Varchar",5,"Modèle");
        relationInfo.addRelaInfo("Integer","Vitesse");
        RelationInfo relationInfo1 = new RelationInfo("Avion", 2);
        relationInfo1.addRelaInfo("Real", "Modèle");
        relationInfo1.addRelaInfo("Integer",10, "Superficie");
        //relationInfo.removeColRelaInfo("vitesse");
        Catalog.getCatalog().init();
        System.out.println(Catalog.getCatalog().getNbreRelation());
        //Catalog.getCatalog().addRelationInfo(relationInfo);
        //Catalog.getCatalog().addRelationInfo(relationInfo1);
        Catalog.getCatalog().finish();

        //relationInfo.addRelaInfo("Real","Poids");
        System.out.println(relationInfo1.getList().get(1).getTaille());*/
        PageId pageId = new PageId(1, 0);
        String tl = pageId.getFileIdx()+""+ pageId.getPageIdx();
        System.out.println(tl);
    }
}
