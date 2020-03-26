package dnsrelay;

import java.net.UnknownHostException;
import java.util.Map;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Scanner;


public class DNSRelay {

	// DNS ip地址 和端口
	public static final String DNSIP = "202.106.0.20"; // ppt要求
	public static final int PORTOFDNS = 53;
	// 本地监听端口
	public static final int PORTOFLOCAL = 53;
	// 包的最大容量
	private static final int DATALENGTH = 4096;
	byte[] inBuff = new byte[DATALENGTH];
	// 接受包 构造inbuff长度的数据包
	private DatagramPacket rcpacket = new DatagramPacket(inBuff, inBuff.length);
	// 转发包
	private DatagramPacket sendpacket;
	// 解析的域名
	private String domainstr;
	// resolver ip地址和端口
	private InetAddress resolverAdd;
	private int portofresolver;
	// 包类型为ipv6标志
	private boolean IsIPv6= false;
	// 指针，指向当前要解析的包的位置
	int UDPCursor;
	// <key, value> = <packet.id, packet.socket>
	private Map<Integer, IDTransition> mapID= new HashMap<Integer, IDTransition>();
	private byte[] NameofDomaininByte;
	private int domainLength;
        String debuglevel; //记录输入
        boolean level0=false; //debug等级
        String level0str="dnsrelay";
        boolean level1=false;
        String level1str="dnsrelay-d";
        boolean level2=false;
        String level2str="dnsrelay-dd";
	public String getName(byte[] buf) {
		// 解析的域名
		String NameofDomain = "";
		UDPCursor = 12;
		// 某个层级域名的长度
		int length =  Convertint.byte2Int(buf, UDPCursor);
		// 当标志位下一分级域名长度为0时跳出
		do{
			UDPCursor++;
			NameofDomain = NameofDomain+ Convertstring.byte2String(buf, UDPCursor, length) + ".";
			UDPCursor += length; // 指向下一个长度那个byte
			length = Convertint.byte2Int(buf, UDPCursor);
		}while (length != 0) ;
		domainLength = UDPCursor - 12 + 1;
		byte[] NameofDomainInByteIn = new byte[domainLength];
		System.arraycopy(buf, 12, NameofDomainInByteIn, 0, domainLength); // UDPCursor指向0x00，记录一下域名
		NameofDomaininByte = NameofDomainInByteIn;
		UDPCursor++; // 跳过0x00
                // 判断数据包类型是否为IPv6类型。 若是将IPv6的flag设置为True
		if (buf[UDPCursor] == 0x00 && buf[UDPCursor + 1] == 0x1c) {
			IsIPv6 = true;
		}
		UDPCursor += 4; // 指向域名后的一个byte
		// 返回域名并去除末尾的'.'
		return NameofDomain.substring(0, NameofDomain.length() - 1);
	}

	public void init() {
            System.out.println("What debug level do you want?\ndnsrelay level0\ndnsrelay-d level1\n"
                        + "dnsrelay-dd level2");
                while(true){
                                    Scanner sc=new Scanner(System.in);
                                    debuglevel=sc.next();                                  
                    if(debuglevel.equals(level0str)) {level0=true; break;}
                    else if(debuglevel.equals(level1str)) {level1=true; break;}
                    else if(debuglevel.equals(level2str)) {level2=true; break;}
                    else System.out.println("ERROR");
                }
                
		DatagramSocket socket = null;
		try {
			// 绑定到53号端口
			socket = new DatagramSocket(PORTOFLOCAL);
			// 持续监听
			do {
				// 接收UDP报文
				socket.receive(rcpacket);
				// 获得DNS数据
                                System.out.println("报文内容：");
                                byte[] sendData = rcpacket.getData();
                                System.out.println(Arrays.toString(sendData));
				// 判断flag是否为查询请求，最高位是不是0，0是query，1是response
				if (((sendData[2] & 0x80) == 0x00)) { // query
					// 获得域名
					domainstr = getName(sendData);
                                        //level2才显示
                                        if(level2)
                                         System.out.println("\n接受时间： " + new java.util.Date());
                                         // 存储报的来源地址和端口号					                                      
					// 本地域名解析表中找到
                                        resolverAdd = rcpacket.getAddress();
					portofresolver = rcpacket.getPort();
                                        if(level1|level2)
					System.out.println("\n域名: " + domainstr);
				
					if (Check.ipTable.containsKey(domainstr)==true)
					{
						// 得到域名对应的IP地址
						String LocalDNSipAddress = Check.ipTable.get(domainstr);
						if (LocalDNSipAddress.equals("0.0.0.0"))
						{// 如果IP为0.0.0.0
                            // 修改标志位response (flag=0x8183)
                            sendData[2] = (byte) (sendData[2] | 0x81);
                            sendData[3] = (byte) (sendData[3] | 0x83);
                            System.out.println("No such name");
							// 回复“no such name”
							// 包装数据并发送                                                        
							sendpacket = new DatagramPacket(sendData,sendData.length, resolverAdd,portofresolver);                                                      
							socket.send(sendpacket);
							IsIPv6= false;
						} else {// 如果不为0.0.0.0 本地组装UDP报文并发回resolver响应
							// 新组装的包
							byte[] finalData = new byte[UDPCursor + domainLength + 14];
							int cur = 0; // answer cursor
							if (IsIPv6) {// 如果是IPv6的请求
					System.out.println("功能：" + "IPv6询问转发到远端DNS");//do nothing
                                               IsIPv6=false;         } else {// IPv4组包返回
								System.out.println("功能：" + "IPv4本地响应");
								// 修改标志位response (flag=0x8180)
								// 设置Answer count 为1
								sendData[2] = (byte) (sendData[2] | 0x81);
								sendData[3] = (byte) (sendData[3] | 0x80);
								sendData[6] = (byte) (sendData[6] | 0x00);
								sendData[7] = (byte) (sendData[7] | 0x01);
								System.arraycopy(sendData, 0, finalData, cur,UDPCursor);
								// 保存name
								cur += UDPCursor;
								System.arraycopy(NameofDomaininByte, 0, finalData, cur, domainLength);
								// 保存typeA，因为是ipv4
								cur += domainLength;
								short typeA = (short) 0x0001;
                                                                //   从指定源数组中复制一个数组，复制从指定的位置开始，到目标数组的指定位置结束。
								System.arraycopy(Convertshort.short2Byte(typeA), 0, finalData, cur, 2);
								// 保存classA, 一般都为1，internet类的
								cur += 2;
								short classA = (short) 0x0001;
								System.arraycopy(Convertshort.short2Byte(classA), 0, finalData, cur, 2);
								// 保存timeLive
								cur += 2;
								int timeLive = 172800; // 两天 ttl
								System.arraycopy(Convertint.int2Byte(timeLive), 0, finalData, cur, 4);
								// 保存responseIPLen
								cur += 4;
								short responseIPLen = (short) 0x0004;
								System.arraycopy(Convertshort.short2Byte(responseIPLen), 0, finalData, cur, 2);
								// 保存responseIP
								cur += 2;
								byte[] responseIP = InetAddress.getByName(Check.ipTable.get(domainstr)).getAddress();
								System.arraycopy(responseIP, 0, finalData, cur, 4);
								cur += 4;
                                                                
                                                             
                                // 响应请求，发送UDP报文
                                sendpacket = new DatagramPacket(finalData,finalData.length, resolverAdd,portofresolver);
                                socket.send(sendpacket);
                                
							}
						}
					} else {// 本地域名解析表中没有找到
						// 发送到远端DNS请求
						sendpacket = new DatagramPacket(sendData,sendData.length, InetAddress.getByName(DNSIP),PORTOFDNS);
                        socket.send(sendpacket);
                            if(mapID.size()>=10000)
                        {
                        	mapID.clear();
                        	System.out.println("Succeed to release the memory");
                        }

                          IsIPv6= false;
                        System.out.println("功能：" + "询问转发到远端DNS");
                        // id存储
                        IDTransition idTransition = new IDTransition((int)Convertshort.byte2Short(sendData, 0), portofresolver, resolverAdd);
                        mapID.put(idTransition.getSrcID(), idTransition);
					}
				} else {// response
					// 收到数据包
					int responseID =  Convertshort.byte2Short(sendData, 0);
					byte[] responseNameofDomain = new byte[domainLength];
					System.arraycopy(sendData, 12, responseNameofDomain, 0, domainLength);
					if (mapID.containsKey(responseID)) {
						IDTransition id = mapID.get(responseID);
							sendpacket = new DatagramPacket(sendData,sendData.length, id.getAddr(), id.getPort());
							socket.send(sendpacket);
                                                mapID.remove(responseID);// 释放空间						
					}
				}
			
                        }while (true);
		} catch (Exception e) {;
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		try {
			Check.readData("dnsrelay.txt");// 读取数据
			System.out.println("读取数据" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Name Server: " + DNSIP);
		System.out.println("Debug Level: 0");
		System.out.println("Bind UDP port " + PORTOFLOCAL + " ...OK!");
		System.out.println("Try to load table \"dnsrelay.txt\" " + " ...OK!");
		System.out.println(Check.ipTable.size() + " names," + "occupy "
				+ Check.fileSize + " bytes memory");
		System.out.println("========================================");
		(new DNSRelay()).init();// 开始程序
	}
}
