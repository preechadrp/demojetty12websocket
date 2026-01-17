-jetty 12.0.x
-web socket แบบไม่ใช้ annotaion ทำแบบ programmatic เพื่อให้รองรับ graalVM
-webapp directory ต้องอยู่ข้างนอก ช่วยเรื่องตอนใช้ graalVM
-logback.xml อยู่ข้างในได้เพราะ realonly file แต่ห้ามใช้ option scanfile
 และเพิ่ม resource-config.json ตอนใช้ graalVM
 หากใช้ scanfile ต้องเอาออกมาข้างนอกแบบ webapp directory
 
 
 ตัวอย่าง resource-config.json
 {
  "resources": {
    "includes": [
      { "pattern": "webapp/.*" }
    ]
  }
}

เหตุผล:
Native image ไม่มี real URL filesystem
classpath resource ไม่ map เป็น directory จริง
Jetty DefaultServlet ต้องการ resource ที่ list ได้
