package Interface;

import android.content.Context;

import java.util.List;

import Utils.NameCard;

/**
 * Created by Wentao on 2015/12/29.
 *
 * Define the interface of controlling database
 */
public interface DatabaseController {

    String DATABASE_NAME = "NameCardStore.db";

    /**
     * [Synchorized Method]
     * Query all of Name cards from Database, it may cost some times, should use in onCreate method
     * @param context Context of where method is used
     * @return return a list of NameCard Class
     */
    public List<NameCard> getCardList(Context context);

    /**
     * [Asynchorized Method]
     * Insert a list of cards to DataBase
     * @param context Context of where method is used
     * @param cardList list of cards should be insertrd into database
     */
    public void insertCardList(Context context, List<NameCard> cardList);

    /**
     * [Asynchorized Method]
     * According to id is exsited, do update or insert one NameCard to DataBase
     * @param context Context of where method is used
     * @param nameCard The NameCard mean to insert or update
     */
    public void saveCard(Context context, NameCard nameCard);

    /**
     * [Asynchorized Method]
     * Delete tone line from DataBase where id = NameCard.id
     * @param context Context of where method is used
     * @param nameCard the card should be deleted
     */
    public void deleteCard(Context context, NameCard nameCard);

    /**
     * [Asynchorized Method]
     * Delete tone line from DataBase where id = NameCard.id
     * @param context Context of where method is used
     * @param cardList list of cards should be deleted
     */
    public void deleteCardList(Context context, List<NameCard> cardList);

    public void saveCardPhotoPath(Context context, NameCard nameCard);
}
