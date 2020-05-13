package com.com.kadzaliklab;

public class MatahariBulanMeeus {

    public static void main(String[] args) {



        int tanggal= 16;
        int bulan=12;
        int tahun=2009;
        double timezone=0;


        double jam=0;
        double menit=0;
        double detik=0;
        double pukul_keJD= (jam*3600+(menit*60)+detik) /86400;

        System.out.println("\n\t\t\t\t\t\t\t"+"Program Ephemeris Algoritma Meeus & Elp2000");
        System.out.println("\t\t\t\t\t\t\t\t\t\t"+tanggal+"-"+bulan+"-"+tahun+"\n");

        double derajat_bujur=112;
        double menit_bujur=13;
        double detik_bujur=0;
        double bujur=derajat_ke_desimal(derajat_bujur,menit_bujur,detik_bujur);

        double derajat_lintang=-7;
        double menit_lintang=47;
        double detik_lintang=0;
        double lintang=derajat_ke_desimal(derajat_lintang,menit_lintang,detik_lintang);
        double lintang_r=Math.toRadians(lintang);



        //waktu lokal ke UT
        int UT= (int) (jam-timezone);

        //hitung nilai Julian day
        if (bulan<=2){bulan+=12; tahun-=1;}
        int A=0;
        int B=0;

        //hanya ada saat hitung georgerian
        if (tahun>1582){
            A=(tahun/100);
            B=2+(A/4)-A;}
        double JD= 1720994.5+(int)(365.25*tahun)+(int)(30.60001*(bulan+1))+B+tanggal+((jam+menit/60+detik/3600)/24)-(timezone/24);
        double JD_UT=(1720994.5+(int)(365.25*tahun)+(int)(30.60001*(bulan+1))+B+tanggal+((jam+menit/60+detik/3600)/24)-(timezone/24));


        double delta_T=0;

        //JDE waktu TD(Dynamical time)
        double jde=JD_UT+delta_T;

        double T_UT=(JD_UT-2451545)/36525;
        double T_TD=(jde-2451545)/36525;
        double tau=T_TD/10;

        //Greenwich sideral time
        double gst0=6.6973745583+2400.0513369072*T_TD+0.0000258622*T_TD*T_TD;

        //fungsi MOD(?) kalou di Excel/LibreCalc
        if (gst0<0){
            while (gst0<0){
                gst0+=24; }
        }
        else if (gst0>24){
            while (gst0>24){
                gst0-=24; }
        }

        double gstUT=gst0+1.0027379035*UT;
        double gstLokal=((gst0+((jam+(menit/60)+(detik/3600)-timezone))*1.00273790935))%24;
        if (gstUT>=24) gstUT-=24;
        gstLokal/=15;

        //lokal sideral time
        double LST=0;
        if (bujur>0)LST=gstLokal+(bujur/15);
        else LST=gstLokal-(bujur/15);

        if (LST>=24)LST-=24;
        double gstpukul=(280.46061837+360.98564736629*(JD_UT-2451545)+0.000387933*T_UT*T_UT-T_UT*T_UT*T_UT/38710000)%360;
        if (gstpukul<0)gstLokal+=360;
        gstpukul/=15;

        double deltaPsi=Nutasi.deltaPsiDanEpsilon(T_TD)[2];

        double epsilon=Nutasi.deltaPsiDanEpsilon(T_TD)[6];
        double epsilon_r=Math.toRadians(epsilon);

        double gstnampak=gstpukul+deltaPsi*Math.cos(epsilon_r)/15;
        double lstnampak=(gstnampak+bujur/15)%24;
        if (lstnampak<0)lstnampak+=24;

        //Bulan
        //l1= bujur rata-rata bulan
        double L1=(218.3164591+481267.88134236*T_TD-0.0013268*T_TD*T_TD+T_TD*T_TD*T_TD/538841-T_TD*T_TD*T_TD*T_TD/65194000)%360;



        //Koreksi bujur bulan
        double koreksibujurB= elp2000.lambdaBulan(T_TD,L1);

        double bujurB_nampak=(elp2000.lambdaBulan(T_TD,L1)+deltaPsi)%360;
        if (bujurB_nampak<0)bujurB_nampak+=360;
        double bujurB_nampak_r=Math.toRadians(bujurB_nampak);
        //Koreksi lintang bulan
        double lintangB= elp2000.lintangBulan(T_TD);
        double lintangB_r=Math.toRadians(lintangB);
        //Koreksi jarak bumi-bulan
        double jarakBB=elp2000.jarakBulan(T_TD);

        double sudutParalaksB=Math.toDegrees(Math.asin(6378.14/jarakBB));
        double sudutJariB=358473400/(jarakBB*3600);

        double alphaBulan=(Math.toDegrees(Math.atan2(Math.sin(bujurB_nampak_r)*Math.cos(epsilon_r)-Math.tan(lintangB_r)*Math.sin(epsilon_r),Math.cos(bujurB_nampak_r))))%360;
        if (alphaBulan<0)alphaBulan=(alphaBulan+360)%360;
        double alphaBulanPukul=alphaBulan/15;
        System.out.println(desimal_ke_derajat(258.4944257661141)[1]+"\u00B0"+desimal_ke_derajat(258.4944257661141)[2]+"\u2032"+desimal_ke_derajat(258.4944257661141)[3]);

        double deltaBulan=Math.toDegrees(Math.asin(Math.sin(lintangB_r)*Math.cos(epsilon_r)+Math.cos(lintangB_r)*Math.sin(epsilon_r)*Math.sin(bujurB_nampak_r)));
        double deltaBulan_r=Math.toRadians(deltaBulan);

        double hourAngleBulan=lstnampak*15-alphaBulan;
        double haBulan_r=Math.toRadians(hourAngleBulan);

        double azimuthBulanS=Math.toDegrees(Math.atan2(Math.sin(haBulan_r),Math.cos(haBulan_r)*Math.sin(lintang_r)-Math.tan(deltaBulan_r)*Math.cos(lintang_r)));
        double azimuthBulan=(azimuthBulanS+180)%360;

        double altitudeB=Math.toDegrees(Math.asin(Math.sin(lintang_r)*Math.sin(deltaBulan_r)+Math.cos(lintang_r)*Math.cos(deltaBulan_r)*Math.cos(haBulan_r)));

        //Matahari

        double lambdaM =TabelMatahari.bujurEkliptik(tau,bujurB_nampak)[3];
        double lambdaM_r =Math.toRadians(lambdaM);
        double theta_terkoreksi =TabelMatahari.bujurEkliptik(tau,bujurB_nampak)[5];
        double jarakBumi_Matahari =TabelMatahari.jarakBumiMat(tau);
        double jarakBm_M=149598000*jarakBumi_Matahari;
        double lintangM =TabelMatahari.lintangEkliptikB(tau,lambdaM_r)[2];
        double beta_M_r =Math.toRadians(lintangM/3600);
        double koreksiAberasi=-20.4898/(3600*jarakBumi_Matahari);

        double bujurM_nampak=(theta_terkoreksi+deltaPsi+koreksiAberasi)%360;
        if (bujurM_nampak<0)bujurM_nampak+=360;
        double bujurM_nampak_r=Math.toRadians(bujurM_nampak);
        double sudutJariM=(959.63/3600)/jarakBumi_Matahari;

        double alphaMatahari=(Math.toDegrees(Math.atan2(Math.sin(bujurM_nampak_r)*Math.cos(epsilon_r)-Math.tan(beta_M_r)*Math.sin(epsilon_r),Math.cos(bujurM_nampak_r))))%360;
        if (alphaMatahari<0)alphaMatahari=(alphaMatahari+360)%360;
        double alphaM_pukul=alphaMatahari/15;
        double deltaMatahari=Math.toDegrees(Math.asin(Math.sin(beta_M_r)*Math.cos(epsilon_r)+Math.cos(beta_M_r)*Math.sin(epsilon_r)*Math.sin(bujurM_nampak_r)));
        double deltaM_r=Math.toRadians(deltaMatahari);
        double hourAngleM=lstnampak*15-alphaMatahari;
        double hourAngleM_r=Math.toRadians(hourAngleM);

        double azimuthM_selatan=Math.toDegrees(Math.atan2(Math.sin(hourAngleM_r),Math.cos(hourAngleM_r)*Math.sin(lintang_r)-Math.tan(deltaM_r)*Math.cos(lintang_r)));
        double azimuthMatahari=(azimuthM_selatan+180)%360;

        double altitudeM=Math.toDegrees(Math.asin(Math.sin(lintang_r)*Math.sin(deltaM_r)+Math.cos(lintang_r)*Math.cos(deltaM_r)*Math.cos(hourAngleM_r)));
        double sudutParalaksM=Math.toDegrees(Math.atan(6378.14/jarakBm_M));

        double U=(JD-2451545)/36525;
        //bujur rata2 matahari
        double L0 =Math.toRadians((280.46607+36000.7698*U)%360);
        double EoT=(-1*(1789+237*U)*Math.sin(L0)-(7146-62*U)*Math.cos(L0)+(9934-14*U)*Math.sin(2*L0)-(29+5*U)*Math.cos(2*L0)+(74+10*U)*Math.sin(3*L0)+(320-4*U)*Math.cos(3*L0)-212*Math.sin(4*L0))/1000;

        EoT/=60; //jadikan menit

        double sudutFai=Math.acos(Math.sin(deltaBulan_r)*Math.sin(deltaM_r)+Math.cos(deltaBulan_r)*Math.cos(deltaM_r)*Math.cos(Math.toRadians(alphaBulan-alphaMatahari)));
        double sudutFase=Math.atan2(jarakBm_M*Math.sin(sudutFai),jarakBB-jarakBm_M*Math.cos(sudutFai));


        double iluminasiB=(1+Math.cos(sudutFase))/2;

        //menampilkan hasil
        //formatter jarak antara teks yang di print biar layoutnya rapi
        System.out.println(JD);
        System.out.println(bujurB_nampak);
        String formatter ="%-8s%-15s%-15s%-15s%-15s%-15s%-15s%5s%n";
        System.out.println("\n"+"Data Matahari");
        System.out.printf(formatter,"Jam","Ecliptic","Ecliptic","Right","Apparent","Semi","Kemiringan","Equation");
        System.out.printf(formatter,"Gmt","Longitude","Latitude","Ascension","Declination","Diameter","(Epsilon)","of time");
        System.out.printf(formatter,(int)jam,desimal_ke_derajat(theta_terkoreksi)[1]+"\u00B0"+desimal_ke_derajat(theta_terkoreksi)[2]+"\u2032"+desimal_ke_derajat(theta_terkoreksi)[3]+"\u2033",(float)lintangM,desimal_ke_derajat(alphaMatahari)[1]+":"+desimal_ke_derajat(alphaMatahari)[2]+":"+desimal_ke_derajat(alphaMatahari)[3],desimal_ke_derajat(deltaMatahari)[1]+"\u00B0"+desimal_ke_derajat(deltaMatahari)[2]+"\u2032"+desimal_ke_derajat(deltaMatahari)[3]+"\u2033",desimal_ke_derajat(sudutJariM)[1]+"\u00B0"+desimal_ke_derajat(sudutJariM)[2]+"\u2032"+desimal_ke_derajat(sudutJariM)[3]+"\u2033",desimal_ke_derajat(epsilon)[1]+"\u00B0"+desimal_ke_derajat(epsilon)[2]+"\u2032"+desimal_ke_derajat(epsilon)[3]+"\u2033",desimal_ke_derajat(EoT)[1]+"\u00B0"+desimal_ke_derajat(EoT)[2]+"\u2032"+desimal_ke_derajat(EoT)[3]+"\u2033");


        System.out.println("\n\n"+"Data Bulan");
        System.out.printf(formatter,"Jam","Apparent","Apparent","Right","Apparent","Semi","Horizontal","Iluminasi");
        System.out.printf(formatter,"Gmt","Longitude","Latitude","Ascension","Declination","Diameter","Parallax","Bulan");
        System.out.printf(formatter,(int)jam,desimal_ke_derajat(bujurB_nampak)[1]+"\u00B0"+desimal_ke_derajat(bujurB_nampak)[2]+"\u2032"+desimal_ke_derajat(bujurB_nampak)[3]+"\u2033",desimal_ke_derajat(lintangB)[1]+"\u00B0"+desimal_ke_derajat(lintangB)[2]+"\u2032"+desimal_ke_derajat(lintangB)[3]+"\u2033",desimal_ke_derajat(alphaBulan)[1]+":"+desimal_ke_derajat(alphaBulan)[2]+":"+desimal_ke_derajat(alphaBulan)[3],desimal_ke_derajat(deltaBulan)[1]+"\u00B0"+desimal_ke_derajat(deltaBulan)[2]+"\u2032"+desimal_ke_derajat(deltaBulan)[3]+"\u2033",desimal_ke_derajat(sudutJariB)[1]+"\u00B0"+desimal_ke_derajat(sudutJariB)[2]+"\u2032"+desimal_ke_derajat(sudutJariB)[3]+"\u2033",desimal_ke_derajat(sudutParalaksB)[1]+"\u00B0"+desimal_ke_derajat(sudutParalaksB)[2]+"\u2032"+desimal_ke_derajat(sudutParalaksB)[3]+"\u2033",String.format("%.5f", iluminasiB));

    }


    public  static int []desimal_ke_derajat(double desimal){
        //cek nilai negatif atau bukan
        boolean negatif=false;
        if (desimal<0)negatif=true;

        //ini menghitungnya mengabaikan nilai negatif
        desimal=Math.abs(desimal);
        int jah=(int)desimal;
        double qoh=Math.abs((desimal%1)*60);
        double ni=Math.round((qoh%1)*60);
        //ini pembulatan
        if ((int)ni>59){ni-=60; qoh+=1;}
        if ((int)qoh>59){qoh-=60; jah+=1;}

        //bila negatif
        if (negatif) {
            if (jah==0)
            { if ((int)qoh==0)return new int[]{0, -jah,-(int)qoh,(int)-ni};else return new int[]{0, -jah,-(int)qoh,(int)ni};}

            else return new int[]{0, -jah,(int)qoh,(int)ni}; }

        //bila positif
        else return new int[]{0,jah,(int)qoh,(int)ni};
    }

    public  static double derajat_ke_desimal(double derajat, double menit, double detik){

        if (derajat<0)
            return derajat-(menit/60)-(detik/3600);
        else return derajat+(menit/60)+(detik/3600);
    }


}
