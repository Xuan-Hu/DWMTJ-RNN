%a1=[0 494 0 261 0 260 0 315 0 354 0 259 0 408 0 333 0 300 0 297 0 336 0 299 0 252 0 210 ];
%a2=[376 471 393 334 316 343 293 318 289 275 314 414 270 279 320 289 223 252 332 318 328 294 248 283 287 304 314 341 ];
%a3=[89 464 240 490 431 560 524 518 521 572 560 577 563 671 696 743 712 716 807 753 903 805 992 913 912 894 974 1019 ];
%a4=[170 183 309 376 460 527 618 714 787 794 802 857 976 915 1024 1096 1037 1133 1157 1127 1266 1215 1428 1420 1608 1559 1645 1631 ];
a1=[0 0 0 0 0 0 0 0 0 0 0 0 0 0];
a2= [376 401 358 302 278 270 308 235 342 333 267 347 398 296];
a3=[106 303 467 512 577 557 671 679 688 765 781 841 907 957 ];
a4=[223 312 440 588 747 882 925 1015 1116 1195 1411 1332 1398 1508 ];
%a1 = a1(1:2:end);
%a2 = a2(1:2:end);
%a3 = a3(1:2:end);
%a4 = a4(1:2:end);

levels=[3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30];
levels=[3 5 7 9 11 13 15 17 19 21 23 25 27 29];

e1= ((1000*levels)-a1)./(1000*levels)*100;
e2= ((1000*levels)-a2)./(1000*levels)*100;
e3= ((1000*levels)-a3)./(1000*levels)*100;
e4= ((1000*levels)-a4)./(1000*levels)*100;

hold on
legend boxoff;

xlim([5 30])
%ylim([0.9 100]);
xlabel('Number of Neurons');
ylabel('Bitwise Accuracy');
plot(levels,e1/100,'o', 'LineWidth',3);
plot(levels,e2/100,'d', 'LineWidth',3);
plot(levels,e3/100,'^', 'LineWidth',3);
%plot(levels,e4,'s', 'LineWidth',3);

ax = gca;
grid off;
box on;
%grid minor
% This sets background color to black.
%ax.Color = 'k'
%ax.YColor = 'r';
% Make the x axis dark green.
%darkGreen = [0, 0.6, 0];
%ax.XColor = darkGreen;
% Make the grid color yellow.
%ax.GridColor = 'k';
%ax.GridAlpha = 0.1; % Set's transparency of the grid.
% Set x and y font sizes.
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

lgd=legend('1 Pattern','2 Patterns','3 Patterns', '4 Patterns');
lgd.FontSize=16;
%lgd.NumColumns=2
legend('Location','southeast');
height = 480;
set(gcf,'position',[10,10,height*8/3,height])
set(gcf,'position',[10,10,height*4/3,height])
hold off
print(gcf,'foo.png','-dpng','-r500');