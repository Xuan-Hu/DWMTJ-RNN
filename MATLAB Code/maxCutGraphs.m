%half=[0.0 1.8 3.9 6.1 8.1 10.1 12.3 15.9 18.8 21.5 25.2 28.6 32.8 36.4 42.1 46.3 52.2 56.9 64.1 70.1 75.7 85.2 89.2 97.1 103.1 109.5 117.0 126.6 135.2 143.7 151.6 159.9 170.2 181.2 194.0 201.9 210.1 225.1 236.4 248.0 257.8 264.5 277.6 290.5 307.0 320.3 332.6 348.8 358.4 367.6 383.4 405.9 414.2 432.8 445.8 457.3 473.5 487.1];
solution = [17.35 20.55 24.1 28.05 32.75 37.25 41.85 46.75 52.4 58.5 64.25 70.35 77.25 84.8 90.85 98.75 106.75 114.6 122.5 131.35 139.65 149.15 158.5 167.6 177.9 188.9 198.95 209.65 220.55 232.1 243.2 255.5 268.05 279.5 293.55 304.85 318.8 331.9 346.25 358.75 374.85 389.4 402.45 418.05 435.3 450.3 466.2 481.45 498.9 516.1 532.25];
halfEst = [12.65 15.5 17.85 20.7 24.9 28.2 31.75 35.95 39.45 45.0 51.25 54.7 59.4 66.15 71.75 79.6 86.5 91.9 99.45 106.9 111.95 121.55 128.15 136.05 144.0 155.75 161.85 170.8 179.15 190.9 197.45 211.6 219.35 234.1 244.45 254.5 265.3 270.65 291.1 300.35 312.85 325.2 341.35 350.1 365.55 379.45 393.75 407.7 421.0 437.55 451.0];
score = [15.95 18.7 20.75 25.0 28.7 33.5 37.25 43.6 46.5 52.95 57.8 63.35 68.5 75.9 81.9 88.05 96.9 103.75 110.8 116.9 126.35 133.8 144.5 152.5 163.6 168.8 181.9 191.1 201.85 210.35 220.35 232.4 239.6 254.8 265.6 279.1 295.65 303.9 318.35 331.4 340.75 357.25 366.4 381.8 401.75 415.45 429.55 446.55 462.6 472.75 491.75];
levels=[10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60];

hold on
%xlim([9 61])%
%ylim([0 500]);

length(solution)
xlabel('Number of Nodes');
ylabel('Normalized Max-cut Score');
%plot(levels,halfEst,'o', 'LineWidth',3);
%plot(levels,score,'o', 'LineWidth',3);

plot(levels,solution./(0.5.*levels.*levels),'o', 'LineWidth',3);
plot(levels,score./(0.5.*levels.*levels),'d', 'LineWidth',3);
plot(levels,halfEst./(0.5.*levels.*levels),'^', 'LineWidth',3);
%plot(levels,solution./(0.5.*levels.*levels).*0.934,'o', 'LineWidth',3);

%plot(levels,score./(0.5.*levels.*levels),'^', 'LineWidth',3);
diffArr=((solution./(0.5.*levels.*levels))-(score./(0.5.*levels.*levels)))./(solution./(0.5.*levels.*levels));
mean(diffArr(10:end))
ax = gca;
grid off;
legend boxoff;
box on;
%grid minor
% This sets background color to black.
%ax.Color = 'k'
%ax.YColor = 'r';
% Make the x axis dark green.
%darkGreen = [0, 0.6, 0];   
ax.XAxis.FontSize = 16;
ax.YAxis.FontSize = 16;
ax.LineWidth=2;
% The below would set everything: title, x axis, y axis, and tick mark label font sizes.
% ax.FontSize = 34;
% Bold all labels.
%ax.FontWeight = 'bold';
%plot(levels,T);
%plot(levels,D);
%errorbar(levels,U,Us);
%errorbar(levels,T,Ts);
%errorbar(levels,D,Ds);

lgd=legend('Optimal Solution','Proposed DW-MTJ Architecture','0.5-Approximation Algorithm');
lgd.FontSize=16;
%lgd.NumColumns=2;
legend('Location','northeast');
height = 480;
set(gcf,'position',[10,10,height*4/3,height])
hold off
print(gcf,'foo.png','-dpng','-r500');