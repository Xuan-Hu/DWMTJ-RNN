weights = [0.02 0.04 0.06 0.08 0.10 0.12 0.14 0.16 0.18 0.20 0.22 0.24];
charges = [0.02 0.04 0.06 0.08 0.10 0.12 0.14 0.16 0.18 0.20 0.22 0.24];
%speedMed25 = [86.372000000009 80.374000000008 79.060000000008 76.872500000008 75.239500000008 74.161000000008 73.404000000007 73.085000000007 73.018500000007 72.650000000007];
%energyMed25 = [4.8592 5.5843 6.6377 7.8276 9.2156 10.8223 12.6450 14.7256 17.0635 19.5416];

%speedMed10 = [78.247500000008 78.204000000008 79.763500000008 77.007000000008 76.461000000008 77.503500000008 76.661000000008 76.992500000008 77.122000000008 76.860000000008];
%energyMed10 = [5.3636 5.4911 5.7251 5.7542 5.9209 6.1920 6.3852 6.6609 6.9468 7.2328];

%constant charge Up Voltage of 0.25 V
%speedMed25 = [111.89700000001 89.120500000009 82.101000000008 80.887500000008 77.958000000008 75.710000000008 74.460000000008 74.275500000008 73.706500000008 73.226000000007 72.786000000007 71.959000000007];
%energyMed25 = [4.0521 4.2274 4.7539 5.6032 6.5798 7.7443 9.1421 10.8361 12.6904 14.7514 17.0124 19.3623];

%constant weight of 0.1V
%speedMed10 = [78.666500000008 77.381000000008 77.247500000008 77.633000000008 76.860000000008 77.934000000008 76.827000000008 78.285000000008 76.350000000008 77.083000000008];
%energyMed10 = [5.3856 5.4478 5.5930 5.7871 5.9418 6.2146 6.3939 6.7287 6.9062 7.2445];

%High Z weights
%constant charge Up Voltage of 0.1 V
speedMed25 = [162.80217000061234 102.01150999990355 92.94746999998364 84.88234000000483 82.19553000000793 78.32001000000801 78.75796000000811 77.50596000000793 76.23719000000777 76.13228000000777 74.97377000000765 74.11357000000757];
energyMed25 =[1.7254             1.5252             2.0557            2.7037            3.6515            4.6532            6.1188            7.6229            9.2711            11.2647           13.2312           15.3939 ]; 

%constant weight of 0.1V
speedMed10 =  [87.35325000000864 85.36725000000874 83.68760000000857 80.52755000000826 81.25585000000834 79.84867000000816 79.74943000000818 82.06111000000843 80.88148000000824 79.42207000000812 79.84085000000815 80.79640000000828];
energyMed10 = [3.7259            3.7415            3.6945            3.5501            3.6021            3.5383            3.5412            3.6696            3.6140            3.5434            3.5713            3.6273];
ax = gca;
% Set x and y font sizes.
ax.XAxis.FontSize = 16;
ax.YAxis.FontSize = 16;
ax.LineWidth=2;

length(weights)
length(speedMed25)
length(energyMed25)

ss=1;

if ss==1
    hold on
    yyaxis left
    grid off
    %legend boxoff;
    box on
    axis([0 0.25 0 20]);
    siz = 20;
    ylabel('Median Energy Consumption (nJ)');
    plot(weights, energyMed25,'.','MarkerSize', siz)
  
    yyaxis right
    ax.YAxis(2).FontSize = 16;
    plot(weights, speedMed25,'.','MarkerSize', siz)
    ylabel('Median Convergence Time (ns)');
    xlabel('Synaptic Weight Voltage (V)')
    %lgd=legend({'Median Energy Consumption','Median Convergence Time'},'Location','northeast');
    %lgd.FontSize=14;

    % BELOW
    in=axes('Position',[.35 .7 .3 .20]);
    plot(in,weights, energyMed25.*speedMed25,'.','MarkerSize', siz)
    energyMed25.*speedMed25
    axis(in,[0 0.25 0 1200]);
    set(gca,'fontsize', 14) 

    xlabel(in,'Synaptic Weight Voltage (V)')
     ylabel(in,'EDP (aJ⋅s)');



elseif ss==2
    hold on
    yyaxis left
    grid on
    box on
    big = axis([0 0.25 3 4]);
    ylabel(big,'Median Energy Consumption (nJ)');
    plot(big,charges, energyMed10,'o','LineWidth',4)
  
    yyaxis right
    ax.YAxis(2).FontSize = 16;
    lgd=legend({'Median Energy Consumption','Median Convergence Time'},'Location','north');
    lgd.FontSize=14;
   
 
    in = axes('Position',[0 0.25 70 90]);
    box on;
    plot(in,weights, energyMed25.*speedMed25,'o','LineWidth',2)
    ylabel(in,'Synaptic Weight Voltage (V)');
    xlabel(in,'nergy Delay Product (aJ⋅s)')


elseif ss==3
    hold on
    %yyaxis left
    grid off;
    %legend boxoff;
    box on
    %axis([0.05 0.25 0 20]);
    ylabel('Energy Delay Product (aJ⋅s)');
    plot(weights, energyMed25.*speedMed25,'o','LineWidth',4)
    axis([0 0.25 0 1200]);
    %yyaxis right
    %ax.YAxis(2).FontSize = 16;
    %plot(weights, speedMed25,'d','LineWidth',3)
    %ylabel('Medium Convergence Time (s)');
    xlabel('Synaptic Weight Voltage (V)')
    %lgd=legend({'EDP','Medium Convergence Time'},'Location','north');
    %lgd.FontSize=14;      
else
    hold on
    %yyaxis left
    grid on
    box on
    %axis([0.05 0.25 0 20]);
    ylabel('Energy Delay Product (aJ⋅s)');
    plot(charges, energyMed10.*speedMed10,'o','LineWidth',4)
    axis([0.1 0.32 400 600]);
    %yyaxis right
    %ax.YAxis(2).FontSize = 16;
    %plot(weights, speedMed25,'d','LineWidth',3)
    %ylabel('Medium Convergence Time (s)');
    xlabel('Charge-Up Voltage (V)')
    %lgd=legend({'EDP','Medium Convergence Time'},'Location','north');
    %lgd.FontSize=14;         
end

height = 480;
set(gcf,'position',[700,1400-2*height,height*4/3,height])
hold off
print(gcf,'foo.png','-dpng','-r500');