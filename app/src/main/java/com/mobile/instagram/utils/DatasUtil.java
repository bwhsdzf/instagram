package com.mobile.instagram.utils;

import com.mobile.instagram.bean.CircleItem;
import com.mobile.instagram.bean.CommentItem;
import com.mobile.instagram.bean.FavortItem;
import com.mobile.instagram.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatasUtil {
	public static final String[] CONTENTS = { "OH MY GOD", "AA", "BB", "CC", "DD", "EE" ,"FF","GG","HH"};
	public static final String[] PHOTOS = {
			"https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Alcazar_de_Segovia.JPG/1200px-Alcazar_de_Segovia.JPG",
			"https://secure.cdn3.wdpromedia.com/resize/mwImage/1/630/354/75/wdpromedia.disney.go.com/media/wdpro-hkdl-assets/prod/en-intl/system/images/sleeping-beauty-castle-hero-00.jpg",
			"https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Zamek_Moszna_-_wschodnie_skrzyd%C5%82o.JPG/400px-Zamek_Moszna_-_wschodnie_skrzyd%C5%82o.JPG",
			"https://i.ytimg.com/vi/SfLV8hD7zX4/maxresdefault.jpg",
			"https://wtop.com/wp-content/uploads/2016/12/ThinkstockPhotos-509640879-727x485.jpg",
			"https://www.merriam-webster.com/assets/mw/images/article/art-wap-article-main/dog-whistle-2450-ae2dc5dad5c3665bab90f51613c230a7@1x.jpg",
			"https://s3.amazonaws.com/cdn-origin-etr.akc.org/wp-content/uploads/2017/11/12233437/Entlebucher-Mountain-Dog-On-White-01.jpg",
			"http://pica.nipic.com/2007-10-17/20071017111345564_2.jpg",
			"http://pic4.nipic.com/20091101/3672704_160309066949_2.jpg",
			"http://pic4.nipic.com/20091203/1295091_123813163959_2.jpg",
			"http://pic31.nipic.com/20130624/8821914_104949466000_2.jpg",
			"http://pic6.nipic.com/20100330/4592428_113348099353_2.jpg",
			"http://pic9.nipic.com/20100917/5653289_174356436608_2.jpg",
			"http://img10.3lian.com/sc6/show02/38/65/386515.jpg",
			"http://pic1.nipic.com/2008-12-09/200812910493588_2.jpg",
			"http://pic2.ooopic.com/11/79/98/31bOOOPICb1_1024.jpg" };
	public static final String[] HEADIMG = {
			"https://pix10.agoda.net/hotelImages/179/1790309/1790309_17010715490050188867.jpg?s=1024x768",
			"http://www.feizl.com/upload2007/2014_06/1406272351394618.png",
			"http://v1.qzone.cc/avatar/201308/30/22/56/5220b2828a477072.jpg%21200x200.jpg",
			"http://v1.qzone.cc/avatar/201308/22/10/36/521579394f4bb419.jpg!200x200.jpg",
			"http://v1.qzone.cc/avatar/201408/20/17/23/53f468ff9c337550.jpg!200x200.jpg",
			"http://cdn.duitang.com/uploads/item/201408/13/20140813122725_8h8Yu.jpeg",
			"http://p2.gexing.com/G1/M00/DA/44/rBACE1Or-q6DO1B3AAAhdSV6v7o914_200x200_3.jpg?recache=20131108",
			"http://p1.qqyou.com/touxiang/uploadpic/2013-3/12/2013031212295986807.jpg"};
	public static final String[] DATES={"11-3","12-6","9-5","6-9","2-9","8-7","4-3","1-5"};
    public static final String[] LOCATIONS={"Paris","Berlin","Melbourne","Shanghai","Adelaide","Tokyo","Sydney","Ottawa"};

	public static List<User> users = new ArrayList<User>();
	private static int userId = 0;
	private static int UserId= 0;
	private static int circleId = 0;
	private static int favortId = 0;
	private static int commentId = 0;
	public static final User curUser = new User("0", "Me", HEADIMG[0],LOCATIONS[0],DATES[0], CONTENTS[0]);
	static {
		User user1 = new User("1", "Alice", HEADIMG[1],LOCATIONS[1],DATES[1], CONTENTS[1]);
		User user2 = new User("2", "Bob", HEADIMG[2],LOCATIONS[2],DATES[2], CONTENTS[2]);
		User user3 = new User("3", "Katty", HEADIMG[3],LOCATIONS[3],DATES[3], CONTENTS[3]);
		User user4 = new User("4", "Zoe", HEADIMG[4],LOCATIONS[4],DATES[4], CONTENTS[4]);
		User user5 = new User("5", "Po", HEADIMG[5],LOCATIONS[5],DATES[5], CONTENTS[5]);
		User user6 = new User("6", "Naoki", HEADIMG[6],LOCATIONS[6],DATES[6], CONTENTS[6]);
		User user7 = new User("7", "Oliver", HEADIMG[7],LOCATIONS[7],DATES[7], CONTENTS[7]);

		users.add(curUser);
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		users.add(user5);
		users.add(user6);
		users.add(user7);
	}

	public static List<CircleItem> createCircleDatas() {
		List<CircleItem> circleDatas = new ArrayList<CircleItem>();
		for (int i = 0; i < users.size(); i++) {
			CircleItem item = new CircleItem();
			User user = getUser();
			item.setId(String.valueOf(circleId++));
			item.setUser(user);
			item.setContent(getContent());
			/*item.setDate(getDate());
            item.setLocation(getLocation());*/

			item.setFavorters(createFavortItemList());
			item.setComments(createCommentItemList());

			item.setType("2");// 图片
			item.setPhotos(createPhotos());

			circleDatas.add(item);
		}

		return circleDatas;
	}

	public static User getUser() {
		return users.get(UserId++);
	}

	public static String getContent() {
		return CONTENTS[getRandomNum(CONTENTS.length)];
	}
	/*public static String getDate() {
		return DATES[getRandomNum(DATES.length)];
	}
    public static String getLocation() {
        return LOCATIONS[getRandomNum(LOCATIONS.length)];
    }*/

	public static int getRandomNum(int max) {
		Random random = new Random();
		int result = random.nextInt(max);
		return result;
	}

	public static List<String> createPhotos() {
		List<String> photos = new ArrayList<String>();
		String photo = PHOTOS[userId];
		photos.add(photo);
		userId++;
		return photos;
	}

	public static List<FavortItem> createFavortItemList() {
		int size = 0;
		List<FavortItem> items = new ArrayList<FavortItem>();
		List<String> history = new ArrayList<String>();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				FavortItem newItem = createFavortItem();
				String userid = newItem.getUser().getId();
				if (!history.contains(userid)) {
					items.add(newItem);
					history.add(userid);
				} else {
					i--;
				}
			}
		}
		return items;
	}

	public static FavortItem createFavortItem() {
		FavortItem item = new FavortItem();
		item.setId(String.valueOf(favortId++));
		item.setUser(getUser());
		return item;
	}
	
	public static FavortItem createCurUserFavortItem() {
		FavortItem item = new FavortItem();
		item.setId(String.valueOf(favortId++));
		item.setUser(curUser);
		return item;
	}

	public static List<CommentItem> createCommentItemList() {
		List<CommentItem> items = new ArrayList<CommentItem>();
		int size = 0;
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				items.add(createComment());
			}
		}
		return items;
	}

	public static CommentItem createComment() {
		CommentItem item = new CommentItem();
		item.setId(String.valueOf(commentId++));
		item.setContent("Oh");
		User user = getUser();
		item.setUser(user);
		if (getRandomNum(10) % 2 == 0) {
			while (true) {
				User replyUser = getUser();
				if (!user.getId().equals(replyUser.getId())) {
					item.setToReplyUser(replyUser);
					break;
				}
			}
		}
		return item;
	}
	
	/**
	 * 创建发布评论
	 * @return
	 */
	public static CommentItem createPublicComment(String content){
		CommentItem item = new CommentItem();
		item.setId(String.valueOf(commentId++));
		item.setContent(content);
		item.setUser(curUser);
		return item;
	}
	
	/**
	 * 创建回复评论
	 * @return
	 */
	public static CommentItem createReplyComment(User replyUser, String content){
		CommentItem item = new CommentItem();
		item.setId(String.valueOf(commentId++));
		item.setContent(content);
		item.setUser(curUser);
		item.setToReplyUser(replyUser);
		return item;
	}
}
