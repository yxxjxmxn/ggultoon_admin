package com.architecture.admin.libraries.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;

public enum CustomError {

    // ELGI : 로그인 관련 오류
    LOGIN_FAIL("ELGI-9999", "lang.admin.exception.login_fail")        // 관리자 로그인이 실패하였습니다.
    , LOGIN_FAIL_JOIN_ID_DUPLE("ELGI-9998", "lang.admin.exception.id_duple")// 이미 존재하는 아이디입니다.
    , ADMIN_STATE_ERROR("ELGI-9997", "lang.admin.exception.state")// 계정상태를 확인해주세요.
    , ADMIN_INFO_GET_ERROR("ELGI-9996", "lang.admin.exception.info")// 계정 확인에 실패하였습니다.
    , LOGIN_ID_ERROR("ELGI-9997", "lang.admin.placeholder.id")// 아이디를 입력해주세요.
    , LOGIN_PW_ERROR("ELGI-9996", "lang.admin.placeholder.pw")// 패스워드를 입력해주세요.

    // EJOI : 회원가입 관련 오류
    , PASSWORD_CONFIRM("EJOI-9999", "lang.admin.placeholder.pw.confirm")// 패스워드를 동일하게 입력해주세요.
    , ID_DUPLE("EJOI-9998", "lang.admin.exception.id_duple")// 이미 존재하는 아이디입니다.
    , JOIN_ID_ERROR("EJOI-9997", "lang.admin.placeholder.id")// 아이디를 입력해주세요.
    , JOIN_PW_ERROR("EJOI-9996", "lang.admin.placeholder.pw")// 패스워드를 입력해주세요.
    , JOIN_NAME_ERROR("EJOI-9995", "lang.admin.placeholder.name")// 이름을 입력해주세요.

    // EADM : 관리자 관련 오류
    , OLD_PASSWORD_EMPTY("EADM-2999", "lang.admin.exception.old.password.empty") // 이전 비밀번호를 입력해주세요.
    , NEW_PASSWORD_EMPTY("EADM-2998", "lang.admin.exception.new.password.empty") // 변경할 비밀번호를 입력해주세요.
    , PASSWORD_CONFIRM_EMPTY("EADM-2997", "lang.admin.exception.password.confirm.empty") // 비밀번호 확인란을 입력해주세요.
    , IDX_ADMIN_ERROR("EADM-3999", "lang.admin.exception.idx")            // 관리자 idx가 없습니다.
    , SEARCH_TYPE_ERROR("EADM-3998", "lang.common.exception.search.word") // 검색 조건을 설정해주세요.
    , LOGIN_ERROR("EADM-3997", "lang.common.exception.login") // 로그인 후 이용 가능합니다.
    , PASSWORD_CORRESPOND("EADM-3996", "lang.admin.exception.password.correspond") // 변경할 비밀번호가 현재 비밀번호와 같습니다.
    , PASSWORD_NOT_CORRESPOND("EADM-3995", "lang.admin.exception.password.confirm.error") // 변경할 비밀번호와 비밀번호 확인란이 일치하지 않습니다.
    , OLD_PASSWORD_ERROR("EADM-3994", "lang.admin.exception.old.password.error") // 현재 비밀번호가 올바르지 않습니다.

    //ENMU : 메뉴 관련 오류
    , ACCESS_FAIL("ENMU-9999", "lang.common.accessfail")            // 접근 권한이 없습니다.
    , MENU_SWAP_FAIL("ENMU-9998", "lang.menu.exception.swap_fail")  // 메뉴 순서 변경을 실패했습니다.
    , MENU_NAME_FAIL("ENMU-9997", "lang.menu.placeholder.name")     // 이름을 입력해주세요.
    , MENU_LINK_FAIL("ENMU-9996", "lang.menu.placeholder.link")     // 링크를 입력해주세요.
    , MENU_ACCESS_FAIL("ENMU-9995", "lang.menu.exception.access_fail")// 잘못된 접근입니다.

    // EPAY : 결제 관련 오류
    , PAYMENT_IDX_ERROR("EPAY-2999", "lang.payment.exception.idx") // 결제 번호가 없습니다.
    , PAYMENT_METHOD_IDX_EMPTY("EPAY-2998", "lang.payment.exception.method.idx.empty") // 요청하신 결제 수단 상세 정보를 찾을 수 없습니다.
    , PAYMENT_METHOD_STATE_EMPTY("EPAY-2997", "lang.payment.exception.method.state.empty") // 결제 수단 사용 상태를 선택해주세요.
    , PAYMENT_METHOD_AUTOPAY_EMPTY("EPAY-2996", "lang.payment.exception.method.autoPay.empty") // 결제 방식을 선택해주세요.
    , PAYMENT_METHOD_PGNAME_EMPTY("EPAY-2995", "lang.payment.exception.method.pgName.empty") // PG업체명을 입력해주세요.
    , PAYMENT_METHOD_MCHTID_EMPTY("EPAY-2994", "lang.payment.exception.method.mchtId.empty") // 상점아이디를 입력해주세요.
    , PAYMENT_METHOD_LICENSEKEY_EMPTY("EPAY-2993", "lang.payment.exception.method.licenseKey.empty") // 해시체크키를 입력해주세요.
    , PAYMENT_METHOD_AES256KEY_EMPTY("EPAY-2992", "lang.payment.exception.method.aes256Key.empty") // 암호키를 입력해주세요.
    , PAYMENT_METHOD_METHODTYPE_EMPTY("EPAY-2991", "lang.payment.exception.method.methodType.empty") // 결제 수단 종류를 입력해주세요.
    , PAYMENT_METHOD_METHOD_EMPTY("EPAY-2990", "lang.payment.exception.method.method.empty") // 결제 수단 코드를 입력해주세요.
    , PAYMENT_METHOD_CORPPAYCODE_EMPTY("EPAY-2989", "lang.payment.exception.method.corpPayCode.empty") // 간편 결제 코드를 입력해주세요.
    , PAYMENT_METHOD_MCHTNAME_EMPTY("EPAY-2988", "lang.payment.exception.method.mchtName.empty") // 서비스명(한글)을 입력해주세요.
    , PAYMENT_METHOD_MCHTENAME_EMPTY("EPAY-2987", "lang.payment.exception.method.mchtEName.empty") // 서비스명(영어)을 입력해주세요.
    , PAYMENT_METHOD_NOTIURL_EMPTY("EPAY-2986", "lang.payment.exception.method.notiUrl.empty") // 결제 처리 url을 입력해주세요.
    , PAYMENT_METHOD_INFO_EMPTY("EPAY-2985", "lang.payment.exception.method.info.empty") // 설명을 입력해주세요.
    , PAYMENT_PRODUCT_IDX_EMPTY("EPAY-2984", "lang.payment.exception.product.idx.empty") // 요청하신 결제 상품 상세 정보를 찾을 수 없습니다.
    , PAYMENT_PRODUCT_STATE_EMPTY("EPAY-2983", "lang.payment.exception.product.state.empty") // 결제 상품 사용 상태를 선택해주세요.
    , PAYMENT_PRODUCT_TITLE_EMPTY("EPAY-2982", "lang.payment.exception.product.title.empty") // 상품명을 입력해주세요.
    , PAYMENT_PRODUCT_TYPE_EMPTY("EPAY-2981", "lang.payment.exception.product.type.empty") // 상품 구분값을 입력해주세요.
    , PAYMENT_PRODUCT_COIN_EMPTY("EPAY-2980", "lang.payment.exception.product.coin.empty") // 코인 개수를 입력해주세요.
    , PAYMENT_PRODUCT_COINFREE_EMPTY("EPAY-2979", "lang.payment.exception.product.coinFree.empty") // 보너스 코인 개수를 입력해주세요.
    , PAYMENT_PRODUCT_COINFREE2_EMPTY("EPAY-2978", "lang.payment.exception.product.coinFree2.empty") // 보너스코인2 개수를 입력해주세요.
    , PAYMENT_PRODUCT_MILEAGE_EMPTY("EPAY-2977", "lang.payment.exception.product.mileage.empty") // 마일리지 개수를 입력해주세요.
    , PAYMENT_PRODUCT_PRICE_EMPTY("EPAY-2976", "lang.payment.exception.product.price.empty") // 결제 금액을 입력해주세요.
    , PAYMENT_PRODUCT_SORT_EMPTY("EPAY-2975", "lang.payment.exception.product.sort.empty") // 상품 순서를 입력해주세요.
    , PAYMENT_METHOD_METHODNOTI_EMPTY("EPAY-2974", "lang.payment.exception.method.methodNoti.empty") // 결제 결과 메소드를 입력해주세요.
    , PAYMENT_METHOD_PAYMENT_SERVER_EMPTY("EPAY-2973", "lang.payment.exception.method.paymentServer.empty") // 결제 서버 URL을 입력해주세요.
    , PAYMENT_METHOD_CANCEL_SERVER_EMPTY("EPAY-2972", "lang.payment.exception.method.cancelServer.empty") // 결제 취소 서버 URL을 입력해주세요.
    , PAYMENT_STATE_ERROR("EPAY-3999", "lang.payment.exception.state") // 이미 취소된 결제건입니다.
    , PAYMENT_DATE_ERROR("EPAY-3998", "lang.payment.exception.date") // 결제 취소 가능 기간이 아닙니다.
    , PAYMENT_COIN_COUNT_ERROR("EPAY-3997", "lang.payment.exception.coin_count") // 코인의 지급 개수와 잔여 개수가 일치하지 않습니다.
    , PAYMENT_BONUS_COUNT_ERROR("EPAY-3996", "lang.payment.exception.bonus_count") // 보너스 코인의 지급 개수와 잔여 개수가 일치하지 않습니다.
    , PAYMENT_MILEAGE_COUNT_ERROR("EPAY-3995", "lang.payment.exception.mileage_count") // 마일리지의 지급 개수와 잔여 개수가 일치하지 않습니다.
    , PAYMENT_COIN_EXP_ERROR("EPAY-3994", "lang.payment.exception.coin_exp") // 지급받은 코인의 유효기간이 만료되었습니다.
    , PAYMENT_BONUS_EXP_ERROR("EPAY-3993", "lang.payment.exception.bonus_exp") // 지급받은 보너스 코인의 유효기간이 만료되었습니다.
    , PAYMENT_MILEAGE_EXP_ERROR("EPAY-3992", "lang.payment.exception.mileage_exp") // 지급받은 마일리지의 유효기간이 만료되었습니다
    , PAYMENT_COIN_STATE_ERROR("EPAY-3991", "lang.payment.exception.coin.state") // 지급받은 코인이 현재 사용 불가 상태입니다.
    , PAYMENT_BONUS_STATE_ERROR("EPAY-3990", "lang.payment.exception.bonus.state") // 지급받은 보너스 코인이 현재 사용 불가 상태입니다.
    , PAYMENT_MILEAGE_STATE_ERROR("EPAY-3989", "lang.payment.exception.mileage.state") // 지급받은 마일리지가 현재 사용 불가 상태입니다.
    , PAYMENT_METHOD_IDX_ERROR("EPAY-3988", "lang.payment.exception.method.idx") // 요청하신 결제 수단 상세 정보를 찾을 수 없습니다.
    , PAYMENT_PRODUCT_IDX_ERROR("EPAY-3987", "lang.payment.exception.product.idx") // 요청하신 결제 상품 상세 정보를 찾을 수 없습니다.
    , PAYMENT_METHOD_REGISTER_ERROR("EPAY-9999", "lang.payment.exception.method.register") // 결제 수단 등록을 실패하였습니다.
    , PAYMENT_METHOD_DELETE_ERROR("EPAY-9998", "lang.payment.exception.method.delete") // 결제 수단 삭제를 실패하였습니다.
    , PAYMENT_METHOD_MODIFY_ERROR("EPAY-9997", "lang.payment.exception.method.modify") // 결제 수단 수정을 실패하였습니다.
    , PAYMENT_PRODUCT_REGISTER_ERROR("EPAY-9996", "lang.payment.exception.product.register") // 결제 상품 등록을 실패하였습니다.
    , PAYMENT_PRODUCT_DELETE_ERROR("EPAY-9995", "lang.payment.exception.product.delete") // 결제 상품 삭제를 실패하였습니다.
    , PAYMENT_PRODUCT_MODIFY_ERROR("EPAY-9994", "lang.payment.exception.product.modify") // 결제 상품 수정을 실패하였습니다.

    //ECON : contents 관련 오류
    , CONTENTS_TITLE_ERROR("ECON-2999", "lang.contents.exception.title")
    , IDX_CONTENTS_ERROR("ECON-2998", "lang.contents.exception.idx")
    , CONTENTS_CATEGORY_ERROR("ECON-2997", "lang.contents.exception.category.idx")
    , CONTENTS_TAG_IDX_EMPTY("ECON-2996", "lang.contents.exception.tag.idx.empty") // 요청하신 태그 정보를 찾을 수 없습니다.
    , CONTENTS_TAG_STATE_EMPTY("ECON-2995", "lang.contents.exception.tag.state.empty") // 태그 사용 상태를 입력해주세요.
    , CONTENTS_TAG_GROUP_EMPTY("ECON-2994", "lang.contents.exception.tag.group.empty") // 태그 그룹 번호를 입력해주세요.
    , CONTENTS_TAG_NAME_EMPTY("ECON-2993", "lang.contents.exception.tag.name.empty") // 태그 이름을 입력해주세요.
    , CONTENTS_TAG_IDX_ERROR("ECON-3999", "lang.contents.exception.tag.idx") // 요청하신 태그 정보를 찾을 수 없습니다.
    , NO_DATA_CONTENTS_ERROR("ECON-9999", "lang.contents.exception.no_data")
    , CONTENTS_ID_EMPTY("ECON-9998", "lang.contents.placeholder.writer.id")    //작성자 아이디를 입력해주세요.
    , CONTENTS_MEMBERIDX_EMPTY("ECON-9997", "lang.contents.writerSearch.fail") //작성자 조회에 실패하였습니다.
    , CONTENTS_CATEGORY_EMPTY("ECON-9996", "lang.contents.placeholder.category") //카테고리 정보가 없습니다.
    , CONTENTS_CONTENTS_EMPTY("ECON-9995", "lang.contents.placeholder.contents") //내용을 입력해 주세요.
    , CONTENTS_COMMENTSTATE_ERROR("ECON-9994", "lang.contents.exception.commentstate.empty") //댓글상태를 확인해주세요
    , CONTENTS_STATE_ERROR("ECON-9993", "lang.contents.exception.state.empty") //상태를 확인해주세요.
    , CONTENTS_LIKESTATE_ERROR("ECON-9992", "lang.contents.exception.likestate.empty") //좋아요상태를 확인해주세요.
    , CONTENTS_IMAGE_NONE("ECON-9991", "lang.contents.placeholder.image")// 이미지가 없습니다.
    , CONTENTS_IMAGE_CNT("ECON-9990", "lang.contents.exception.imgCnt")// 이미지개수 12개
    , CONTENTS_DELETE_ERROR("ECON-9989", "lang.contents.exception.delete_fail") // 삭제 실패하였습니다.
    , CONTENTS_ID_DUPLE("ECON-8888", "lang.contents.exception.id_duple")// uuid가 중복됩니다
    , CONTENTS_COMMENT_IDX_ERROR("ECON-8887", "lang.contents.exception.comment.idx") // 존재하지 않는 댓글입니다.
    , CONTENTS_COMMENT_DELETE_FAIL("ECON-8886", "lang.contents.exception.comment.delete_fail") // 댓글 삭제에 실패하였습니다.
    , CONTENTS_TAG_REGISTER_ERROR("ECON-8885", "lang.contents.exception.tag.register") // 작품 태그 등록을 실패하였습니다.
    , CONTENTS_TAG_MODIFY_ERROR("ECON-8884", "lang.contents.exception.tag.modify") // 작품 태그 수정을 실패하였습니다.
    , CONTENTS_TAG_DELETE_ERROR("ECON-8883", "lang.contents.exception.tag.delete") // 작품 태그 삭제를 실패하였습니다.
    , CONTENTS_COMPANY_ERROR("ECON-2992", "lang.contents.exception.company") // 제휴사(CP)를 선택해 주세요.

    //EIMG : image 관련 오류
    , IMAGE_RESIZE_ERROR("EIMG-9999", "lang.contents.exception.img.resize") //이미지 리사이즈에 실패하였습니다.
    , NO_IMAGE_FILE_ERROR("EIMG-9998", "lang.contents.exception.img.file") //이미지 파일이 아닙니다.
    , IDX_FILE_IMAGE_ERROR("EIMG-9997", "lang.contents.exception.img.idx") //이미지 idx가 잘못되었습니다.
    , NO_IMAGE_PRODUCT_ERROR("EPRO-9990", "lang.product.exception.image.no") // 이미지 파일이 아닙니다.

    // EMEM : 회원 관련 오류
    , IDX_MEMBER_ERROR("EMEM-9999", "IDX_ERROR")
    , CODE_MEMBER_ERROR("EMEM-9998", "lang.member.exception.code") // 삭제코드를 입력해주세요.
    , LANG_MEMBER_ERROR("EMEM-9997", "lang.member.exception.lang") // 사용언어를 선택해주세요.
    , EMAIL_MEMBER_ERROR("EMEM-9995", "lang.member.exception.email") // 사용이메일을 입력해주세요.
    , PHONE_MEMBER_ERROR("EMEM-9994", "lang.member.exception.phone") // 전화번호를 입력해주세요.
    , BIRTH_MEMBER_ERROR("EMEM-9993", "lang.member.exception.birth") // 생년월일을 입력해주세요.
    , INTRO_MEMBER_ERROR("EMEM-9992", "lang.member.exception.intro") // 소개를 입력해주세요.
    , NICK_MEMBER_ERROR("EMEM-9991", "lang.member.exception.nick") // 닉네임을 입력해주세요.
    , DELETE_MEMBER_ERROR("EMEM-9990","lang.member.exception.deleteFail") // 탈퇴에 실패하였습니다.
    , DUPLICATE_NICK_MEMBER_ERROR("EMEM-9990", "lang.member.exception.check.nick") // 중복된 닉네임입니다.

    // EREP : 신고 관련 오류
    , REPORT_CONTENTS_IDX_EMPTY("EREP-2999", "lang.report.exception.content.idx.empty") // 요청하신 작품 신고 내역을 찾을 수 없습니다.
    , REPORT_CONTENTS_COMMENT_IDX_EMPTY("EREP-2998", "lang.report.exception.content.comment.idx.empty") // 요청하신 작품 댓글 신고 내역을 찾을 수 없습니다.
    , REPORT_EPISODE_COMMENT_IDX_EMPTY("EREP-2997", "lang.report.exception.episode.comment.idx.empty") // 요청하신 회차 댓글 신고 내역을 찾을 수 없습니다.
    , REPORT_CONTENTS_IDX_ERROR("EREP-3999", "lang.report.exception.content.idx.error") // 요청하신 작품 신고 내역을 찾을 수 없습니다.
    , REPORT_CONTENTS_COMMENT_IDX_ERROR("EREP-3998", "lang.report.exception.content.comment.idx.error") // 요청하신 작품 댓글 신고 내역을 찾을 수 없습니다.
    , REPORT_EPISODE_COMMENT_IDX_ERROR("EREP-3997", "lang.report.exception.episode.comment.idx.error") // 요청하신 회차 댓글 신고 내역을 찾을 수 없습니다.
    , REPORT_CONTENTS_DELETE_ERROR("EREP-9999", "lang.report.exception.content.delete") // 작품 신고 내역 삭제를 실패하였습니다.
    , REPORT_CONTENTS_COMMENT_DELETE_ERROR("EREP-9998", "lang.report.exception.content.comment.delete") // 작품 댓글 신고 내역 삭제를 실패하였습니다.
    , REPORT_EPISODE_COMMENT_DELETE_ERROR("EREP-9997", "lang.report.exception.episode.comment.delete") // 회차 댓글 신고 내역 삭제를 실패하였습니다.

    // ECOI : 코인 관련 오류
    , COIN_PAYMENT_BY_ADMIN_ERROR("ECOI-9999", "lang.coin.exception.payment")          // 코인 지급에 실패하였습니다.
    , COIN_SUBTRACT_BY_ADMIN_ERROR("ECOI-9998", "lang.coin.exception.subtract")        // 코인 차감에 실패하였습니다.
    , COIN_PRICE_EMPTY("ECOI-2999", "lang.coin.exception.price.empty")                 // 금액을 입력해주세요.
    , COIN_PAYMENT_PRICE_EMPTY("ECOI-2998","lang.coin.exception.payment.price.empty")  // 결제 금액을 입력해주세요.
    , COIN_PAYMENT_TYPE_EMPTY("ECOI-2997", "lang.coin.exception.payment.type.empty")   // 결제수단을 선택해주세요.
    , COIN_AMOUNT_OVER("ECOI-2996", "lang.coin.exception.coin.amount.over")            // 보유코인보다 차감액이 큽니다.
    , COIN_MILEAGE_AMOUNT_OVER("ECOI-2995", "lang.coin.exception.mileage.amount.over") // 보유 마일리지보다 차감액이 큽니다.
    , COIN_REASON_EMPTY("ECOI-2994", "lang.coin.exception.reason.empty")               // 사유를 입력해주세요.

    // EEPI : 회차 관련 오류
    , IDX_EPISODE_ERROR("EEPI-2999", "lang.episode.exception.idx")
    , NO_DATA_EPISODE_ERROR("EEPI-2998", "lang.episode.exception.no_data")

    // EVIE : 뷰어 관련 오류
    , IDX_VIEWER_ERROR("EVIE-2999", "lang.episode.exception.viewer.idx")

    // ECOM : 댓글 관련 오류
    , COMMENT_DELETE_ERROR("ECOM-9999", "lang.comment.exception.delete") // 댓글 삭제를 실패하였습니다.
    , COMMENT_NORMAL_ERROR("ECOM-9998", "lang.comment.exception.normal") // 댓글 복구를 실패하였습니다.
    , COMMENT_HIDE_ERROR("ECOM-9997", "lang.comment.exception.hide") // 댓글 숨김을 실패하였습니다.
    , COMMENT_SHOW_ERROR("ECOM-9996", "lang.comment.exception.show") // 댓글 노출을 실패하였습니다.
    , COMMENT_IDX_EMPTY("ECOM-2999", "lang.comment.exception.idx.empty") // 요청하신 댓글을 찾을 수 없습니다.
    , COMMENT_IDX_ERROR("ECOM-3999", "lang.comment.exception.idx.error") // 요청하신 댓글을 찾을 수 없습니다.

    // ENOT : 알림 관련 오류
    , NOTIFICATION_IDX_EMPTY("ENOT-2999", "lang.notification.exception.idx.empty") // 요청하신 알림 정보를 찾을 수 없습니다.
    , NOTIFICATION_STATE_EMPTY("ENOT-2998", "lang.notification.exception.state.empty") // 알림 상태를 입력해주세요.
    , NOTIFICATION_TYPE_EMPTY("ENOT-2997", "lang.notification.exception.type.empty") // 알림 종류를 입력해주세요.
    , NOTIFICATION_TITLE_EMPTY("ENOT-2996", "lang.notification.exception.title.empty") // 알림 내용을 입력해주세요.
    , NOTIFICATION_URL_EMPTY("ENOT-2995", "lang.notification.exception.url.empty") // 알림 url을 입력해주세요.
    , NOTIFICATION_IDX_ERROR("ENOT-3999", "lang.notification.exception.idx") // 요청하신 알림 정보를 찾을 수 없습니다.
    , NOTIFICATION_REGISTER_ERROR("ENOT-9999", "lang.notification.exception.register") // 알림 등록을 실패하였습니다.
    , NOTIFICATION_DELETE_ERROR("ENOT-9998", "lang.notification.exception.delete") // 알림 삭제를 실패하였습니다.
    , NOTIFICATION_MODIFY_ERROR("ENOT-9997", "lang.notification.exception.modify") // 알림 수정을 실패하였습니다.

    // EBOR : 고객센터 관련 오류
    , NOTICE_IDX_EMPTY("EBOR-2999", "lang.board.notice.exception.idx.empty") // 요청하신 공지사항 정보를 찾을 수 없습니다.
    , NOTICE_STATE_EMPTY("EBOR-2998", "lang.board.notice.exception.state.empty") // 공지사항 사용 상태를 입력해주세요.
    , NOTICE_MUSTREAD_EMPTY("EBOR-2997", "lang.board.notice.exception.mustRead.empty") // 공지사항 필독 여부를 입력해주세요.
    , NOTICE_TITLE_EMPTY("EBOR-2996", "lang.board.notice.exception.title.empty") // 공지사항 제목을 입력해주세요.
    , NOTICE_CONTENT_EMPTY("EBOR-2995", "lang.board.notice.exception.content.empty") // 공지사항 내용을 입력해주세요.
    , NOTICE_TYPE_EMPTY("EBOR-2994", "lang.board.notice.exception.type.empty") // 공지사항 타입 구분값을 입력해주세요.
    , NOTICE_IDX_ERROR("EBOR-3999", "lang.board.notice.exception.idx") // 요청하신 공지사항 정보를 찾을 수 없습니다.
    , NOTICE_REGISTER_ERROR("EBOR-9999", "lang.board.notice.exception.register") // 공지사항 등록을 실패하였습니다.
    , NOTICE_DELETE_ERROR("EBOR-9998", "lang.board.notice.exception.delete") // 공지사항 삭제를 실패하였습니다.
    , NOTICE_MODIFY_ERROR("EBOR-9997", "lang.board.notice.exception.modify") // 공지사항 수정을 실패하였습니다.
    , NOTICE_SEND_ALARM_ERROR("EBOR-9996", "lang.board.notice.exception.send") // 공지사항 알림 전송을 실패하였습니다.

    // EAIP : 관리자 허용 IP 관련 오류
    , ADMIN_IP_IDX_EMPTY("EAIP-2999", "lang.ip.exception.idx.empty") // 요청하신 IP 상세 정보를 찾을 수 없습니다.
    , ADMIN_IP_STATE_EMPTY("EAIP-2998", "lang.ip.exception.state.empty") // IP 사용 상태를 선택해주세요.
    , ADMIN_IP_TYPE_EMPTY("EAIP-2997", "lang.ip.exception.type.empty") // 관리자 구분값을 선택해주세요.
    , ADMIN_IP_EMPTY("EAIP-2996", "lang.ip.exception.ip.empty") // IP 주소를 입력해주세요.
    , ADMIN_IP_MEMO_EMPTY("EAIP-2995", "lang.ip.exception.memo.empty") // IP 주소 설명을 입력해주세요.
    , ADMIN_IP_IDX_ERROR("EAIP-3999", "lang.ip.exception.idx") // 요청하신 IP 상세 정보를 찾을 수 없습니다.
    , ADMIN_IP_REGISTER_ERROR("EAIP-9999", "lang.ip.exception.register") // IP 등록을 실패하였습니다.
    , ADMIN_IP_DELETE_ERROR("EAIP-9998", "lang.ip.exception.delete") // IP 삭제를 실패하였습니다.
    , ADMIN_IP_MODIFY_ERROR("EAIP-9997", "lang.ip.exception.modify") // IP 수정을 실패하였습니다.
    , ADMIN_IP_ACCESS_ERROR("EAIP-9996", "lang.ip.exception.access") // 접근이 허용되지 않은 IP입니다.

    // ECUR : 작품 큐레이션 관련 오류
    , CURATION_IDX_EMPTY("ECUR-2999", "lang.curation.exception.idx.empty") // 요청하신 큐레이션 상세 정보를 찾을 수 없습니다.
    , CURATION_STATE_EMPTY("ECUR-2998", "lang.curation.exception.state.empty") // 큐레이션 사용 상태를 선택해주세요.
    , CURATION_AREA_EMPTY("ECUR-2997", "lang.curation.exception.area.empty") // 큐레이션 노출 영역을 선택해주세요.
    , CURATION_TITLE_EMPTY("ECUR-2996", "lang.curation.exception.title.empty") // 큐레이션 제목을 입력해주세요.
    , CURATION_RESERVATION_EMPTY("ECUR-2994", "lang.curation.exception.reservation.empty") // 큐레이션 예약 여부를 선택해주세요.
    , CURATION_RESERVATION_DATE_EMPTY("ECUR-2993", "lang.curation.exception.reservation.date.empty") // 큐레이션 예약 기간을 선택해주세요.
    , CURATION_AREA_IDX_EMPTY("ECUR-2992", "lang.curation.exception.area.idx.empty") // 요청하신 큐레이션 노출 영역 상세 정보를 찾을 수 없습니다.
    , CURATION_AREA_STATE_EMPTY("ECUR-2991", "lang.curation.exception.area.state.empty") // 큐레이션 노출 영역 사용 상태를 선택해주세요.
    , CURATION_AREA_CODE_EMPTY("ECUR-2990", "lang.curation.exception.area.code.empty") // 큐레이션 노출 영역 코드를 입력해주세요.
    , CURATION_AREA_NAME_EMPTY("ECUR-2989", "lang.curation.exception.area.name.empty") // 큐레이션 노출 영역 이름을 입력해주세요.
    , CURATION_CONTENT_IDX_EMPTY("ECUR-2988", "lang.curation.exception.content.idx.empty") // 요청하신 큐레이션 매핑 작품 정보를 찾을 수 없습니다.
    , CURATION_CONTENT_STATE_EMPTY("ECUR-2987", "lang.curation.exception.content.state.empty") // 큐레이션 매핑 작품 사용 상태를 선택해주세요.
    , CURATION_CONTENT_IDX_DUPLICATED("ECUR-3999", "lang.curation.exception.content.idx.duplicated") // 현재 큐레이션에 이미 등록된 작품입니다.
    , CURATION_REGISTER_ERROR("ECUR-9999", "lang.curation.exception.register") // 큐레이션 등록을 실패하였습니다.
    , CURATION_DELETE_ERROR("ECUR-9998", "lang.curation.exception.delete") // 큐레이션 삭제를 실패하였습니다.
    , CURATION_MODIFY_ERROR("ECUR-9997", "lang.curation.exception.modify") // 큐레이션 수정을 실패하였습니다.
    , CURATION_REORDER_ERROR("ECUR-9996", "lang.curation.exception.reorder") // 순서 변경을 실패하였습니다.
    , CURATION_AREA_REGISTER_ERROR("ECUR-9995", "lang.curation.exception.area.register") // 큐레이션 노출 영역 등록을 실패하였습니다.
    , CURATION_AREA_DELETE_ERROR("ECUR-9994", "lang.curation.exception.area.delete") // 큐레이션 노출 영역 삭제를 실패하였습니다.
    , CURATION_AREA_MODIFY_ERROR("ECUR-9993", "lang.curation.exception.area.modify") // 큐레이션 노출 영역 수정을 실패하였습니다.
    , CURATION_CONTENT_REGISTER_ERROR("ECUR-9992", "lang.curation.exception.content.register") // 큐레이션 매핑 작품 등록을 실패했습니다.
    , CURATION_CONTENT_DELETE_ERROR("ECUR-9991", "lang.curation.exception.content.delete") // 큐레이션 매핑 작품 삭제를 실패했습니다.
    , CURATION_CONTENT_MODIFY_ERROR("ECUR-9990", "lang.curation.exception.content.modify") // 큐레이션 매핑 작품 수정을 실패했습니다.

    // EBAN : 배너 관련 오류
    , BANNER_TITLE_ERROR("EBAN-2999", "lang.banner.exception.title")        // 배너 제목을 입력해주세요.
    , BANNER_START_DATE_ERROR("EBAN-2998", "lang.banner.exception.startDate")   // 배너 예약일을 입력해주세요.
    , BANNER_END_DATE_ERROR("EBAN-2997", "lang.banner.exception.endDate")   // 배너 예약일을 입력해주세요.
    , BANNER_IMAGE_ERROR("EBAN-2996", "lang.banner.exception.image")        // 배너 이미지를 등록해 주세요.
    , BANNER_IDX_ERROR("EBAN-2995", "lang.banner.exception.idx")            // 배너를 찾을수 없습니다.
    , BANNER_NO_DATA_ERROR("EBAN-2994", "lang.banner.exception.no_data")    // 배너 정보가 없습니다.

    // ETIC : 선물함 관련 오류
    , TICKET_GROUP_IDX_EMPTY("ETIC-2999", "lang.ticket.group.exception.idx") // 요청하신 그룹 상세 정보를 찾을 수 없습니다.
    , TICKET_GROUP_STATE_EMPTY("ETIC-2998", "lang.ticket.group.exception.state.empty") // 그룹 사용 상태를 선택해주세요.
    , TICKET_GROUP_CODE_EMPTY("ETIC-2997", "lang.ticket.group.exception.code.empty") // 그룹 코드를 선택해주세요.
    , TICKET_GROUP_NAME_EMPTY("ETIC-2996", "lang.ticket.group.exception.name.empty") // 그룹 이름을 입력해주세요.
    , TICKET_GROUP_DESCRIPTION_EMPTY("ETIC-2995", "lang.ticket.group.exception.description.empty") // 그룹 설명을 입력해주세요.
    , TICKET_CONTENTS_IDX_EMPTY("ETIC-2994", "lang.ticket.exception.content.empty") // 작품을 선택해주세요.
    , TICKET_GROUP_EMPTY("ETIC-2993", "lang.ticket.exception.group.empty") // 지급 대상 그룹을 선택해주세요.
    , TICKET_GROUP_AGE_EMPTY("ETIC-2992", "lang.ticket.exception.group.age.empty") // 지급 연령을 선택해주세요.
    , TICKET_COUNT_EMPTY("ETIC-2991", "lang.ticket.exception.count.empty") // 지급할 이용권 개수를 입력해주세요.
    , TICKET_EXCEPT_EPISODE_EMPTY("ETIC-2990", "lang.ticket.exception.except.episode.empty") // 이용권 지급 제외 회차 개수를 입력해주세요.
    , TICKET_PERIOD_EMPTY("ETIC-2989", "lang.ticket.exception.except.period.empty") // 이용권 사용 유효기간을 입력해주세요.
    , TICKET_AVAILABLE_DATE_EMPTY("ETIC-2988", "lang.ticket.exception.available.date.empty") // 이용권 사용 가능 기간을 입력해주세요.
    , TICKET_IDX_EMPTY("ETIC-2987", "lang.ticket.exception.idx") // 요청하신 이용권 상세 정보를 찾을 수 없습니다.
    , TICKET_MODIFY_STATE_ERROR("ETIC-3999", "lang.ticket.exception.modify.state") // 지급이 진행 중인 이용권은 수정할 수 없습니다.
    , TICKET_DELETE_STATE_ERROR("ETIC-3998", "lang.ticket.exception.delete.state") // 지급이 진행 중인 이용권은 삭제할 수 없습니다.
    , TICKET_SELL_TYPE_ERROR("ETIC-3997", "lang.ticket.exception.sell.type") // 소장만 가능한 작품은 이용권에 등록할 수 없습니다.
    , TICKET_AVAILABLE_DATE_ERROR("ETIC-3996", "lang.ticket.exception.available.date") // 이용권 사용 가능 기간이 유효하지 않습니다.
    , TICKET_EXCEPT_COUNT_ERROR("ETIC-3995", "lang.ticket.exception.except.count") // 이용권 지급 제외 회차 개수가 유효하지 않습니다.
    , TICKET_GROUP_MODIFY_ERROR("ETIC-9999", "lang.ticket.group.exception.modify") // 그룹 수정을 실패했습니다.
    , TICKET_REGISTER_ERROR("ETIC-9998", "lang.ticket.exception.register") // 이용권 등록을 실패하였습니다.
    , TICKET_DELETE_ERROR("ETIC-9997", "lang.ticket.exception.delete") // 이용권 삭제를 실패하였습니다.
    , TICKET_MODIFY_ERROR("ETIC-9996", "lang.ticket.exception.modify") // 이용권 수정을 실패하였습니다.

    // ECOU : 쿠폰 관련 오류
    , COUPON_STORE_IDX_EMPTY("ECOU-2999", "lang.coupon.store.exception.idx") // 요청하신 쿠폰 업체 상세 정보를 찾을 수 없습니다.
    , COUPON_STORE_STATE_EMPTY("ECOU-2998", "lang.coupon.store.exception.state.empty") // 업체 상태를 선택해주세요.
    , COUPON_STORE_NAME_EMPTY("ECOU-2997", "lang.coupon.store.exception.name.empty") // 업체 이름을 입력해주세요.
    , COUPON_STORE_TYPE_EMPTY("ECOU-2996", "lang.coupon.store.exception.type.empty") // 업체 유형을 선택해주세요.
    , COUPON_IDX_EMPTY("ECOU-2995", "lang.coupon.exception.idx") // 요청하신 쿠폰 상세 정보를 찾을 수 없습니다.
    , COUPON_STATE_EMPTY("ECOU-2994", "lang.coupon.exception.state.empty") // 쿠폰 상태를 선택해주세요.
    , COUPON_TITLE_EMPTY("ECOU-2993", "lang.coupon.exception.title.empty") // 쿠폰 이름을 입력해주세요.
    , COUPON_CODE_EMPTY("ECOU-2992", "lang.coupon.exception.code.empty") // 쿠폰 코드를 입력해주세요.
    , COUPON_DUPLE_EMPTY("ECOU-2991", "lang.coupon.exception.duplication.empty") // 쿠폰 중복 여부를 선택해주세요.
    , COUPON_TYPE_EMPTY("ECOU-2990", "lang.coupon.exception.type.empty") // 쿠폰 종류를 선택해주세요.
    , COUPON_COIN_EMPTY("ECOU-2989", "lang.coupon.exception.coin.empty") // 지급할 코인 개수를 입력해주세요.
    , COUPON_MILEAGE_EMPTY("ECOU-2988", "lang.coupon.exception.mileage.empty") // 지급할 마일리지 개수를 입력해주세요.
    , COUPON_COUNT_EMPTY("ECOU-2987", "lang.coupon.exception.count.empty") // 쿠폰 발급 개수를 입력해주세요.
    , COUPON_AVAILABLE_DATE_EMPTY("ECOU-2986", "lang.coupon.exception.date.empty") // 쿠폰 사용 가능 기간을 입력해주세요.
    , COUPON_STORE_MANAGER_PHONE_ERROR("ECOU-3999", "lang.coupon.store.exception.manager.phone") // 업체 담당자 연락처가 유효하지 않습니다.
    , COUPON_AVAILABLE_DATE_ERROR("ECOU-3998", "lang.coupon.exception.date.error") // 쿠폰 사용 가능 기간이 유효하지 않습니다.
    , COUPON_DELETE_STATE_ERROR("ECOU-3997", "lang.coupon.exception.delete.state") // 지급이 진행중이거나 완료된 쿠폰은 삭제할 수 없습니다.
    , COUPON_MODIFY_STATE_ERROR("ECOU-3996", "lang.coupon.exception.modify.state") // 지급이 진행중이거나 완료된 쿠폰은 수정할 수 없습니다.
    , COUPON_STORE_REGISTER_ERROR("ECOU-9999", "lang.coupon.store.exception.register") // 쿠폰 업체 등록을 실패하였습니다.
    , COUPON_STORE_DELETE_ERROR("ECOU-9998", "lang.coupon.store.exception.delete") // 쿠폰 업체 삭제를 실패하였습니다.
    , COUPON_STORE_MODIFY_ERROR("ECOU-9997", "lang.coupon.store.exception.modify") // 쿠폰 업체 수정을 실패하였습니다.
    , COUPON_REGISTER_ERROR("ECOU-9996", "lang.coupon.exception.register") // 쿠폰 등록을 실패하였습니다.
    , COUPON_DELETE_ERROR("ECOU-9995", "lang.coupon.exception.delete") // 쿠폰 삭제를 실패하였습니다.
    , COUPON_MODIFY_ERROR("ECOU-9994", "lang.coupon.exception.modify") // 쿠폰 수정을 실패하였습니다.
    ;

    private String code;
    private String message;

    @Autowired
    MessageSource messageSource;

    CustomError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
    }

    public CustomError setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        return this;
    }

    @Component
    public static class EnumValuesInjectionService {

        @Autowired
        private MessageSource messageSource;

        // bean
        @PostConstruct
        public void postConstruct() {
            for (CustomError customError : EnumSet.allOf(CustomError.class)) {
                customError.setMessageSource(messageSource);
            }
        }
    }
}
