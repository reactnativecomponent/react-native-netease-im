export enum NIMResponseCode {
  /**
   * Successful operation
   */
  RES_SUCCESS = "200",

  /**
   * Incorrect password
   */
  RES_EUIDPASS = "302",

  /**
   * Login IP or MAC is banned
   */
  RES_ADDR_BLOCKED = "310",

  /**
   * Internal accounts are not allowed to log in at this address
   */
  RES_IP_NOT_ALLOWED = "315",

  /**
   *The version number is too old and needs to be upgraded
   */
  RES_VERSION_EXPIRED = "317",

  RES_NEED_RECONNECT2 = "398",
  /**
   *Need to reconnect
   * Do not pass it to the main process in ResponseDispatcherPush#makeResponse, but throw it directly through RemoteAgent#response elsewhere.
   */
  RES_NEED_RECONNECT = "398",

  /**
   * Need to switch LBS
   * Do not pass it to the main process in ResponseDispatcherPush#makeResponse, but throw it directly through RemoteAgent#response elsewhere.
   */
  RES_NEED_CHANGE_LBS = "399",

  /**
   * App banned
   */
  RES_FORBIDDEN = "403",

  /**
   * The target (object or user) does not exist
   */
  RES_ENONEXIST = "404",

  /**
   * Operation timeout
   */
  RES_ETIMEOUT = "408",

  /**
   * Parameter error
   */
  RES_EPARAM = "414",

  /**
   * There is a problem with the network connection
   */
  RES_ECONNECTION = "415",

  /**
   * Operate too frequently
   */
  RES_EFREQUENTLY = "416",

  /**
   * The object already exists
   */
  RES_EEXIST = "417",

  /**
   *Account disabled
   */
  RES_ACCOUNT_BLOCK = "422",

  /**
   * The device is not in the trusted device list
   */
  RES_DEVICE_NOT_TRUST = "431",

  /**
   * Server internal error
   */
  RES_EUNKNOWN = "500",

  /**
   * Failed to operate database
   */
  RES_DB_EXCEPTION = "502",

  /**
   * Server is too busy
   */
  RES_TOOBUZY = "503",

  /**
   * overdue
   */
  RES_OVERDUE = "508",

  /**
   * has expired
   */
  RES_INVALID = "509",

  /**
   * Red envelope function is not available
   */
  RES_RP_INVALID = "515",

  /**
   * Successfully cleared the unread part of the session
   */
  RES_CLEAR_UNREAD_PART_SUCCESS = "700",

  //Group error code
  /**
   * Number of people limit reached
   */
  RES_TEAM_ECOUNT_LIMIT = "801",

  /**
   * permission denied
   */
  RES_TEAM_ENACCESS = "802",

  /**
   * The group does not exist
   */
  RES_TEAM_ENOTEXIST = "803",

  /**
   * The user is not in the group
   */
  RES_TEAM_EMEMBER_NOTEXIST = "804",

  /**
   * Wrong group type
   */
  RES_TEAM_ERR_TYPE = "805",

  /**
   *The number of groups reaches the upper limit
   */
  RES_TEAM_LIMIT = "806",

  /**
   * The group member status is incorrect
   */
  RES_TEAM_USER_STATUS_ERR = "807",

  /**
   * Application successful
   */
  RES_TEAM_APPLY_SUCCESS = "808",

  /**
   * The user is already in the group
   */
  RES_TEAM_ALREADY_IN = "809",

  /**
   *Invitation successful
   */
  RES_TEAM_INVITE_SUCCESS = "810",

  /**
   * Some invitations are successful, and there will also be a list of failures.
   */
  RES_TEAM_INVITE_PART_SUCCESS = "813",

  /**
   * Some requests are successful, and there will also be a failure list
   */
  RES_TEAM_INFO_PART_SUCCESS = "816",

  /**
   * internal error
   */
  RES_EPACKET = "999",

  /**
   * internal error
   */
  RES_EUNPACKET = "998",

  /**
   * Blacklisted by the recipient
   */
  RES_IN_BLACK_LIST = "7101",

  /**
   * **************************** chatroom******************* *******************
   */

  /**
   *IM main connection status is abnormal
   */
  RES_CHATROOM_IM_LINK_EXCEPTION = "13001",

  /**
   * Chat room status is abnormal
   */
  RES_CHATROOM_STATUS_EXCEPTION = "13002",

  /**
   * The account is in the blacklist and is not allowed to enter the chat room
   */
  RES_CHATROOM_BLACKLIST = "13003",

  /**
   * In the ban list, speech is not allowed
   */
  RES_CHATROOM_MUTED = "13004",

  /**
   * The chat room is in a muted state and only the administrator can speak
   */
  RES_CHATROOM_ROOM_MUTED = "13006",

  /**
   * ********************************* Independent signaling system************ ***************
   */

  /**
   * The operation performed has been successful, but the other party is not online (the push is reachable, but offline)
   */
  RES_PEER_NIM_OFFLINE = "10201",

  /**
   * The operation performed has been successful, but the other party's push is unreachable.
   */
  RES_PEER_PUSH_OFFLINE = "10202",
  /**
   * The corresponding channel does not exist
   */
  RES_CHANNEL_NOT_EXISTS = "10404",

  /**
   * The corresponding channel already exists
   */
  RES_CHANNEL_HAS_EXISTS = "10405",

  /**
   * Not in the channel
   */
  RES_CHANNEL_MEMBER_NOT_EXISTS = "10406",

  /**
   *Already in the channel
   */
  RES_CHANNEL_MEMBER_HAS_EXISTS = "10407",
  /**
   * The invitation does not exist or has expired
   */
  RES_INVITE_NOT_EXISTS = "10408",
  /**
   *The invitation has been declined
   */
  RES_INVITE_HAS_REJECT = "10409",

  /**
   * Invitation has been accepted
   */
  RES_INVITE_HAS_ACCEPT = "10410",

  /**
   * Add channel uid conflict
   */
  RES_JOIN_CHANNEL_UID_CONFLICT = "10417",

  /**
   * The number of people in the channel exceeds the limit
   */
  RES_CHANNEL_MEMBER_EXCEED = "10419",
  /**
   * Already in the channel (own other end)
   */
  RES_CHANNEL_MEMBER_HAS_EXISTS_OTHER_CLIENT = "10420",

  /**
   * The third-party callback custom error code returns the minimum value, the range is [20000, 20099], the specific error code correspondence is determined by the user
   */
  RES_CALLBACK_CUSTOM_CODE_MIN = "20000",

  /**
   * The third-party callback custom error code returns the maximum value, 20000-20099
   */
  RES_CALLBACK_CUSTOM_CODE_MAX = "20099",

  ///////////////////////////////////////////////////// //////
  /////////////////////// Local error code ////////////////////////
  ///////////////////////////////////////////////////// /////

  /**
   * unknown
   */
  RES_UNKNOWN = "-1",

  /**
   * Abnormal operation occurs
   */
  RES_EXCEPTION = "1000",

  /**
   * SDK callback upper layer operation is abnormal.
   */
  RES_CALLBACK_APP_EXCEPTION = "1001",

  /**
   * SDK asynchronous to synchronous call timeout
   */
  RES_API_SYNC_TIMEOUT = "1002",

  /**
   * SDK asynchronous to synchronous calls occur on threads with Looper (synchronous calls are not allowed on threads with Looper)
   */
  RES_API_SYNC_RUN_ON_LOOPER_THREAD_EXCEPTION = "1003",

  /**
   * A runtime exception occurs when SDK asynchronous to synchronous call (such as thread interrupted exception)
   */
  RES_API_SYNC_RUNTIME_EXCEPTION = "1004",

  /**
   * Currently not online and unable to operate
   */
  RES_OFFLINE = "1",

  /**
   * Currently unsupported operations
   */
  RES_UNSUPPORT = "2",

  /**
   * Failed to register third-party push SDK to obtain token
   */
  RES_REGISTER_PUSH_SDK_FAILED = "3",

  /**
   * Failed to download expired files (corresponding to nos token scene)
   */
  DOWNLOAD_EXPIRE_FILE = "4",

  /**
   * An illegal scene key is used
   */
  USE_INVALID_SCENE = "5",

  /**
   *File not found
   */
  FILE_NOT_FOUND = "6",

  /**
   *File upload failed
   */
  FILE_UPLOAD_ERROR = "7",

  /**
   * Registering third-party push SDK timeout
   */
  RES_REGISTER_PUSH_SDK_TIME_OUT = "8",
}

export interface NIMCommonErrorType {
  code: NIMResponseCode;
  message: string;
  domain: string;
  userInfo: any;
}

export enum NIMCommonBooleanType {
  FALSE = "0",
  TRUE = "1",
}
