package net.swordie.ms.connection.packet;

import net.swordie.ms.enums.DimensionalPortalType;
import net.swordie.ms.life.npc.NpcScriptInfo;
import net.swordie.ms.scripts.ScriptManagerImpl;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.life.npc.NpcMessageType;
import net.swordie.ms.scripts.ScriptType;
import net.swordie.ms.handlers.header.OutHeader;
import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * Created on 2/19/2018.
 */
public class ScriptMan {

    public static OutPacket scriptMessage(NpcScriptInfo nsi, NpcMessageType nmt) {
        var outPacket = new OutPacket(SCRIPT_MESSAGE);

        outPacket.encodeByte(nsi.getSpeakerType());
        int overrideTemplate = nsi.getOverrideSpeakerTemplateID();
        outPacket.encodeInt(overrideTemplate != 0 ? overrideTemplate : nsi.getTemplateID());
        outPacket.encodeByte(overrideTemplate > 0);
        if(overrideTemplate > 0) {
            outPacket.encodeInt(overrideTemplate);
        }
        outPacket.encodeByte(nmt.getVal());
        outPacket.encodeByte(nsi.getParam());
        outPacket.encodeByte(nsi.getColor());

        switch(nmt) {
            case Say:
            case SayOk:
            case SayNext:
            case SayPrev:
                if((nsi.getParam() & 4) != 0) {
                    outPacket.encodeInt(nsi.getOverrideSpeakerTemplateID());
                }
                outPacket.encodeString(nsi.getText());
                outPacket.encodeByte(nmt.isPrevPossible());
                outPacket.encodeByte(nmt.isNextPossible());
                outPacket.encodeInt(nmt.getDelay());
                break;
            case AskMenu:
            case AskAccept:
            case AskYesNo:
                if((nsi.getParam() & 4) != 0) {
                    outPacket.encodeInt(nsi.getOverrideSpeakerTemplateID());
                }
                outPacket.encodeString(nsi.getText());
                break;
            case SayImage:
                String[] images = nsi.getImages();
                outPacket.encodeByte(images.length);
                for(String image : images) {
                    outPacket.encodeString(image);
                }
                break;
            case AskText:
                if((nsi.getParam() & 4) != 0) {
                    outPacket.encodeInt(nsi.getOverrideSpeakerTemplateID());
                }
                outPacket.encodeString(nsi.getText());
                outPacket.encodeString(nsi.getDefaultText());
                outPacket.encodeShort(nsi.getMin());
                outPacket.encodeShort(nsi.getMax());
                break;
            case AskNumber:
                outPacket.encodeString(nsi.getText());
                outPacket.encodeInt(nsi.getDefaultNumber());
                outPacket.encodeInt(nsi.getMin());
                outPacket.encodeInt(nsi.getMax());
                break;
            case InitialQuiz:
                outPacket.encodeByte(nsi.getType());
                if(nsi.getType() != 1) {
                    outPacket.encodeString(nsi.getTitle());
                    outPacket.encodeString(nsi.getProblemText());
                    outPacket.encodeString(nsi.getHintText());
                    outPacket.encodeInt(nsi.getMin());
                    outPacket.encodeInt(nsi.getMax());
                    outPacket.encodeInt(nsi.getTime()); // in seconds
                }
                break;
            case InitialSpeedQuiz:
                outPacket.encodeByte(nsi.getType());
                if(nsi.getType() != 1) {
                    outPacket.encodeInt(nsi.getQuizType());
                    outPacket.encodeInt(nsi.getAnswer());
                    outPacket.encodeInt(nsi.getCorrectAnswers());
                    outPacket.encodeInt(nsi.getRemaining());
                    outPacket.encodeInt(nsi.getTime()); // in seconds
                }
                break;
            case ICQuiz:
                outPacket.encodeByte(nsi.getType());
                if(nsi.getType() != 1) {
                    outPacket.encodeString(nsi.getText());
                    outPacket.encodeString(nsi.getHintText());
                    outPacket.encodeInt(nsi.getTime()); // in seconds
                }
                break;
            case AskAvatar:
                int[] options = nsi.getOptions();
                outPacket.encodeByte(nsi.isAngelicBuster());
                outPacket.encodeByte(nsi.isZeroBeta());
                outPacket.encodeString(nsi.getText());
                outPacket.encodeByte(options.length);
                for (int option : options) {
                    outPacket.encodeInt(option);
                }
                break;
            case AskSlideMenu:
                outPacket.encodeInt(nsi.getDlgType());
                // start CSlideMenuDlg::SetSlideMenuDlg
                outPacket.encodeInt(0); // last selected
                StringBuilder sb = new StringBuilder();
                for (DimensionalPortalType dpt : DimensionalPortalType.values()) {
                    if (dpt.getMapID() != 0) {
                        sb.append("#").append(dpt.getVal()).append("#").append(dpt.getDesc());
                    }
                }
                outPacket.encodeString(sb.toString());
                outPacket.encodeInt(0);
                break;
            case AskSelectMenu:
                outPacket.encodeInt(nsi.getDlgType());
                if (nsi.getDlgType() <= 0 || nsi.getDlgType() == 1) {
                    outPacket.encodeInt(nsi.getDefaultSelect());
                    outPacket.encodeInt(nsi.getSelectText().length);
                    for (String selectText : nsi.getSelectText()) {
                        outPacket.encodeString(selectText);
                    }
                }
                break;
        }
        if ((nsi.getParam() & 4) != 0) {
            nsi.setParam((byte) (nsi.getParam() ^ 4));
        }

        return outPacket;
    }

}
