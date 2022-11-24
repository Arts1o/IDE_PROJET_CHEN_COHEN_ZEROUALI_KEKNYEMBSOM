import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.Format;

public class FileManager
{
    private static FileManager fileManager = new FileManager();
    private int headV = 0;
    private int dataV = 0;

    private FileManager(){
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public PageId createNewHeaderPage() throws IOException {
        //int flagdirt = -1;
        PageId pageId = DiskManager.getLeDiskManager().allocPage();
        ByteBuffer bm = BufferManager.getInstance().getPage(pageId);
        bm.putInt(0);
        //bm.getInt(bm.getInt()+1);
        //DiskManager.getLeDiskManager().WritePage(pageId,bm.putInt(0));
        freeP(pageId);
        return pageId;
    }

    public PageId addDataPage(RelationInfo relInfo) throws IOException {
        PageId pageId = DiskManager.getLeDiskManager().allocPage();
        ByteBuffer bm = BufferManager.getInstance().getPage(pageId);
        bm.putInt(0);
        bm.putInt(0);
        //ByteBuffer byteBuffer = ByteBuffer.wrap("00000000".getBytes());
        //DiskManager.getLeDiskManager().writeAtEnd(pageId,byteBuffer);

        freeP(pageId);
        DiskManager.getLeDiskManager().WritePage(relInfo.getHeaderPageId(), ecrirePageIdToBuff(pageId));
        return pageId;
    }

    public PageId getFreeDataPageId(RelationInfo relInfo,int sizeRecord){
        if (relInfo.getHeaderPageId()!= null){
            
        }
        return null;
    }

    public ByteBuffer ecrirePageIdToBuff(PageId pageId)
    {
        String tmp = pageId.getFileIdx() + "" + pageId.getPageIdx();
        return ByteBuffer.wrap(tmp.getBytes());
    }


    private void freeP(PageId pageId){
        int flagdirt = -1;
        Frame[] frames = BufferManager.getInstance().getFrame();
        for (Frame frame : frames)
        {
            if (frame.getPID().compareTo(pageId)) {
                flagdirt = frame.getDirty();
                break;
            }
        }
        if (flagdirt != -1)
            BufferManager.getInstance().FreePage(pageId,flagdirt-1);
        else
            System.out.println("Page non  trouvé dans la liste des Frames");
    }

    public PageId lirePageIdToBuff(ByteBuffer buf, boolean first)
    {
        int pageIdint = first ? buf.getInt(0) : buf.getInt(3);

        int fileIdx = pageIdint / 10;
        int pageIdx = pageIdint % 10;
        PageId pageId = new PageId(fileIdx, pageIdx);

        return pageId;
    }
}
