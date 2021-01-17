package org.bytediff.display;


import org.bytediff.engine.DiffInfo;

public class BasicDisplay {
    private DiffInfo info;

    private BasicDisplay(DiffInfo details) {
        this.info = details;
    }

    public static BasicDisplay from(DiffInfo details) {
        return new BasicDisplay(details);
    }

    public String display() {
        String sourceS = new String(this.info.getSource(), this.info.getCharset());
        String targetS = new String(this.info.getTarget(), this.info.getCharset());

        StringBuilder sb = new StringBuilder();
        for (DiffInfo.Info info : this.info.getInfo()) {
            if (info.getInfoType() == DiffInfo.InfoType.INSERT) {
                int start = info.getSourceStart();
                int end = info.getSourceEnd();
                String insertion = sourceS.substring(start, sourceS.offsetByCodePoints(start, end - start + 1));
                sb.append("INSERTION: ").append(insertion).append("\n");
            } else if (info.getInfoType() == DiffInfo.InfoType.DELETE) {
                int start = info.getSourceStart();
                int end = info.getSourceEnd();
                String deletion = sourceS.substring(start, sourceS.offsetByCodePoints(start, end - start + 1));
                sb.append("DELETION: ").append(deletion).append("\n");
            } else if (info.getInfoType() == DiffInfo.InfoType.MATCH) {
                int start = info.getSourceStart();
                int end = info.getSourceEnd();
                String matching = sourceS.substring(start, sourceS.offsetByCodePoints(start, end - start + 1));
                sb.append("MATCH: ").append(matching).append("\n");
            } else {
                int start = info.getSourceStart();
                int end = info.getSourceEnd();
                String replaceFrom = sourceS.substring(start, sourceS.offsetByCodePoints(start, end - start + 1));
                start = info.getSourceStart();
                end = info.getSourceEnd();
                String replaceTo = sourceS.substring(start, sourceS.offsetByCodePoints(start, end - start + 1));
                sb.append("REPLACE: ").append(replaceFrom).append(" ==> ").append(replaceTo)
                        .append("\n");
            }
        }
        return sb.toString();
    }
}
